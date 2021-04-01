//package com.possenti.gateway
//
//import org.slf4j.LoggerFactory
//import org.springframework.cloud.gateway.filter.GatewayFilterChain
//import org.springframework.cloud.gateway.filter.GlobalFilter
//import org.springframework.core.Ordered
//import org.springframework.stereotype.Component
//import org.springframework.web.server.ServerWebExchange
//import reactor.core.publisher.Mono
//import java.util.function.Consumer
//
//
//@Component
//class MyPreFilter : GlobalFilter, Ordered {
//
//    val logger = LoggerFactory.getLogger(MyPreFilter::class.java)
//
//    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
//        logger.info("My first Pre-filter is executed...")
//        val requestPath = exchange.request.path.toString()
//        logger.info("Request path = $requestPath")
//        val headers = exchange.request.headers
//        val headerNames: Set<String> = headers.keys
//        headerNames.forEach(Consumer { headerName: String ->
//            val headerValue = headers.getFirst(headerName)
//            logger.info("$headerName $headerValue")
//        })
//        return chain.filter(exchange)
//    }
//
//    override fun getOrder(): Int {
//        return 0
//    }
//}