package com.possenti.gateway

import com.google.common.net.HttpHeaders
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
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
        return label@ GatewayFilter { exchange: ServerWebExchange, chain: GatewayFilterChain ->
            val request = exchange.request
            if (!request.headers.containsKey(HttpHeaders.AUTHORIZATION)) {
                return@GatewayFilter onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED)
            }
            val authorizationHeader = request.headers[HttpHeaders.AUTHORIZATION]!![0]
            val jwt = authorizationHeader.replace("Bearer", "")
            if (!isJwtValid(jwt)) {
                return@GatewayFilter onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED)
            }
            chain.filter(exchange)
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
            subject = Jwts.parser().setSigningKey(env!!.getProperty("token.secret")).parseClaimsJws(jwt).body
                    .subject
        } catch (ex: Exception) {
            returnValue = false
        }
        if (subject == null || subject.isEmpty()) {
            returnValue = false
        }
        return returnValue
    }
}