package com.yogaveda.auth.routing

import com.yogaveda.auth.model.User
import com.yogaveda.auth.routing.request.VerifiedUserRequest
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

fun Route.userRoute(
    userService: UserService
) {
    /**
     * User Registration
     * Creates a new user if user does not exists.
     * If user already exists, then just return the tokens
     */
    post("register") {

        // receive a verified user
        val verifiedUser = call.receive<VerifiedUserRequest>()
        val authResponse = userService.registerUser(verifiedUser)

        authResponse?.let {
            call.response.header(
                name = "id",
                value = authResponse.id
            )

            call.respond(
                status = HttpStatusCode.Created,
                message = authResponse
            )
        } ?: return@post call.respond(
            HttpStatusCode.BadRequest
        )

    }

    /**
     * User Login
     * Authenticates the user and returns the tokens
     */
    post("login") {

        val verifiedUserRequest = call.receive<VerifiedUserRequest>()
        val authResponse = userService.authenticate(verifiedUserRequest)

        authResponse?.let {
            call.respond(it)
        } ?: call.respond(HttpStatusCode.Unauthorized)

    }

    authenticate {
        /**
         * Get all users
         */
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

            if (foundUser.email != extractEmailFromPrincipal(call))
                return@get call.respond(HttpStatusCode.Forbidden)

            call.respond(
                HttpStatusCode.OK,
                foundUser.toResponse()
            )
        }
    }
}

fun extractEmailFromPrincipal(call: io.ktor.server.application.ApplicationCall): String? =
    call.principal<JWTPrincipal>()?.getClaim("email", String::class)


fun User.toResponse() = UserResponse(
    id = id,
    email = email
)