package com.possenti.gateway

import com.google.common.net.HttpHeaders
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Component
class AuthorizationHeaderFilter : AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config>(Config::class.java) {
    @Autowired
    var env: Environment? = null

    class Config { // Put configuration properties here
    }

    override fun apply(config: Config): GatewayFilter {
        return label@ GatewayFilter { exchange, chain ->

            val token = exchange.request.headers[HttpHeaders.AUTHORIZATION]?.firstOrNull()

            if (token.isNullOrBlank())
                return@GatewayFilter onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED)

            if (token.startsWith("Bearer").not())
                return@GatewayFilter onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED)

            val subject = getSubjectFromToken(token)
            if (subject.isNullOrEmpty())
                return@GatewayFilter onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED)

            val newExchange = addUserEmailHeader(subject, exchange)

            chain.filter(newExchange)
        }
    }

    private fun onError(exchange: ServerWebExchange, err: String, httpStatus: HttpStatus): Mono<Void> {
        val response = exchange.response
        response.statusCode = httpStatus
        return response.setComplete()
    }

    private fun addUserEmailHeader(subject: String, exchange: ServerWebExchange): ServerWebExchange {
        val newRequest = exchange.request.mutate().header("x-user-email", subject).build()
        return exchange.mutate().request(newRequest).build()
    }

    private fun getSubjectFromToken(token: String) = kotlin.runCatching {
        val jwt = token.replace("Bearer", "")
        Jwts.parser().setSigningKey(env!!.getProperty("token.secret")).parseClaimsJws(jwt).body.subject
    }.getOrNull()

}