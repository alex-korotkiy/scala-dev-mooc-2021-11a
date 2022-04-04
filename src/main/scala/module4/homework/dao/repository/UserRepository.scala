package module4.homework.dao.repository

import zio.Has

import io.getquill.context.ZioJdbc.QIO
import module4.homework.dao.entity.User
import zio.macros.accessible
import zio.{ULayer, ZLayer}
import module4.homework.dao.entity.{Role, UserToRole}
import module4.homework.dao.entity.UserId
import module4.homework.dao.entity.RoleCode
import module4.phoneBook.db


object UserRepository{


    val dc = db.Ctx
    import dc._

    type UserRepository = Has[Service]

    trait Service{
        def findUser(userId: UserId): QIO[Option[User]]
        def createUser(user: User): QIO[User]
        def createUsers(users: List[User]): QIO[List[User]]
        def updateUser(user: User): QIO[Unit]
        def deleteUser(user: User): QIO[Unit]
        def findByLastName(lastName: String): QIO[List[User]]
        def list(): QIO[List[User]]
        def userRoles(userId: UserId): QIO[List[Role]]
        def insertRoleToUser(roleCode: RoleCode, userId: UserId): QIO[Unit]
        def listUsersWithRole(roleCode: RoleCode): QIO[List[User]]
        def findRoleByCode(roleCode: RoleCode): QIO[Option[Role]]
    }

    class ServiceImpl extends Service{

        lazy val userSchema = quote{
            querySchema[User](""""User"""")
        }

        lazy val roleSchema = quote{
            querySchema[Role](""""Role"""")
        }

        lazy val userToRoleSchema = quote{
            querySchema[UserToRole](""""UserToRole"""")
        }

        def findUser(userId: UserId): Result[Option[User]] =
            dc.run(userSchema.filter(_.id == lift(userId.id))).map(_.headOption)
        
        def createUser(user: User): Result[User] =
            dc.run(userSchema.insert(lift(user)).returning(x => x))
        
        def createUsers(users: List[User]): Result[List[User]] = {
            zio.ZIO.collectAll(users.map(u => createUser(u)))
        }

        def updateUser(user: User): Result[Unit] =
            dc.run(userSchema.filter(_.id == lift(user.id)).update(lift(user))).map(_ => ())
        
        def deleteUser(user: User): Result[Unit] =
            dc.run(userSchema.filter(_.id == lift(user.id)).delete).map(_ => ())
        
        def findByLastName(lastName: String): Result[List[User]] =
            dc.run(userSchema.filter(_.lastName == lift(lastName)))
        
        def list(): Result[List[User]] = dc.run(userSchema)

        def userRoles(userId: UserId): Result[List[Role]] =
            dc.run(
                for {
                    userToRole <- userToRoleSchema.filter(_.userId == lift(userId.id))
                    roles <- roleSchema.join(_.code == userToRole.roleId)
                } yield roles
            )
        
        def insertRoleToUser(roleCode: RoleCode, userId: UserId): Result[Unit] =
            dc.run(userToRoleSchema.insert(lift(UserToRole(roleCode.code, userId.id)))).map(_ => ())
        
        def listUsersWithRole(roleCode: RoleCode): Result[List[User]] =
            dc.run(
                for {
                    userRole <- userToRoleSchema.filter(_.roleId == lift(roleCode.code))
                    users <- userSchema.join(_.id == userRole.userId)
                } yield (users)
            )
        
        def findRoleByCode(roleCode: RoleCode): Result[Option[Role]] =
            dc.run(roleSchema.filter(_.code == lift(roleCode.code))).map(_.headOption)
                
    }

    val live: ULayer[UserRepository] = ZLayer.succeed(new ServiceImpl)
}