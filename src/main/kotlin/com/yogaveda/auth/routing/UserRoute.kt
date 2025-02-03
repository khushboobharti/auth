package com.yogaveda.auth.routing

import com.yogaveda.auth.model.User
import com.yogaveda.auth.routing.request.UserRequest
import com.yogaveda.auth.routing.response.UserResponse
import com.yogaveda.auth.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.get
import java.util.UUID

fun Route.userRoute(
    userService: UserService
) {
    post {
        val userRequest = call.receive<UserRequest>()

        val createdUser = userService.save(
            userRequest.toModel()
        ) ?: return@post call.respond(
            HttpStatusCode.BadRequest
        )

        call.response.header(
            name = "id",
            value = createdUser.id.toString()
        )

        call.respond(
            HttpStatusCode.Created
        )
    }

    authenticate {
        get {

            val users = userService.findAll()
            call.respond(
                HttpStatusCode.OK,
                message = users.map { it.toResponse() }
            )

        }
    }


    authenticate("admin-auth") {
        get("/{id}") {
            val id = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val foundUser = userService.findById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound)

            if (foundUser.username != extractUsernameFromPrincipal(call))
                return@get call.respond(HttpStatusCode.Forbidden)

            call.respond(
                HttpStatusCode.OK,
                foundUser.toResponse()
            )
        }
    }
}

fun extractUsernameFromPrincipal(call: io.ktor.server.application.ApplicationCall): String? =
    call.principal<JWTPrincipal>()?.getClaim("username", String::class)

fun UserRequest.toModel() = User(
    id = UUID.randomUUID(),
    username = username,
    password = password
)

fun User.toResponse() = UserResponse(
    id = id,
    username = username
)