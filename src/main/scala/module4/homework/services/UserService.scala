package module4.homework.services

import zio.Has
import zio.Task
import module4.homework.dao.entity.{Role, RoleCode, User, UserId, UserToRole}
import module4.homework.dao.repository.UserRepository
import zio.interop.catz._
import zio.ZIO
import zio.RIO
import zio.ZLayer
import zio.macros.accessible
import module4.phoneBook.db

@accessible
object UserService{
    type UserService = Has[Service]

    trait Service{
        def listUsers(): RIO[db.DataSource, List[User]]
        def listUsersDTO(): RIO[db.DataSource, List[UserDTO]]
        def addUserWithRole(user: User, roleCode: RoleCode): RIO[db.DataSource, UserDTO]
        def listUsersWithRole(roleCode: RoleCode): RIO[db.DataSource, List[UserDTO]]
    }

    class Impl(userRepo: UserRepository.Service) extends Service{
        val dc = db.Ctx
        import dc._

        def listUsers(): RIO[db.DataSource, List[User]] =
            userRepo.list()

        def attachRoles(users: List[User]): RIO[db.DataSource, List[UserDTO]] =
            ZIO.collectAll(
                users.map(u => {
                    for {
                        roles <- userRepo.userRoles(UserId(u.id))
                    } yield UserDTO(u, roles.toSet)
            }))

        def listUsersDTO(): RIO[db.DataSource,List[UserDTO]] =
            listUsers().flatMap(attachRoles)

        def addUserWithRole(user: User, roleCode: RoleCode): RIO[db.DataSource, UserDTO] =
            dc.transaction(
                for {
                   u <- userRepo.createUser(user)
                   _ <- userRepo.insertRoleToUser(roleCode, UserId(u.id))
                   roles <- userRepo.userRoles(UserId(u.id))
               } yield UserDTO(u, roles.toSet)
            )

        def listUsersWithRole(roleCode: RoleCode): RIO[db.DataSource,List[UserDTO]] =
            userRepo.listUsersWithRole(roleCode).flatMap(attachRoles)
        
        
    }

    val live: ZLayer[UserRepository.UserRepository, Nothing, UserService] = ZLayer.fromService(repo => new Impl(repo))
}

case class UserDTO(user: User, roles: Set[Role])