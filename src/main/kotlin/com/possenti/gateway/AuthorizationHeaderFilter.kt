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

@Component
class AuthorizationHeaderFilter : AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config>(Config::class.java) {
    @Autowired
    var env: Environment? = null

    class Config { // Put configuration properties here
    }

    override fun apply(config: Config): GatewayFilter {
        return label@GatewayFilter { exchange, chain ->

            val token = exchange.request.headers[HttpHeaders.AUTHORIZATION]?.firstOrNull()

            if (token.isNullOrBlank())
                return@GatewayFilter onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED)

            val jwt = token.replace("Bearer", "")
            if (isJwtValid(jwt).not())
                return@GatewayFilter onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED)


            val newExchange = addUserEmailHeader(jwt, exchange)

            chain.filter(newExchange)
        }
    }

    private fun onError(exchange: ServerWebExchange, err: String, httpStatus: HttpStatus): Mono<Void> {
        val response = exchange.response
        response.statusCode = httpStatus
        return response.setComplete()
    }

    private fun isJwtValid(jwt: String): Boolean {
        var returnValue = true
        var subject: String? = null
        try {
            subject = getSubjectFromToken(jwt)
        } catch (ex: Exception) {
            returnValue = false
        }
        if (subject == null || subject.isEmpty()) {
            returnValue = false
        }
        return returnValue
    }

    private fun addUserEmailHeader(jwt: String, exchange: ServerWebExchange) : ServerWebExchange{
        val subject = getSubjectFromToken(jwt)
        val newRequest = exchange.request.mutate().header("x-user-email", subject).build()
        return exchange.mutate().request(newRequest).build()
    }

    private fun getSubjectFromToken(jwt: String) = Jwts.parser().setSigningKey(env!!.getProperty("token.secret"))
            .parseClaimsJws(jwt).body.subject

}