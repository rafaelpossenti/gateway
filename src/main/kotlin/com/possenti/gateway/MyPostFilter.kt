//package com.possenti.gateway
//
//import org.slf4j.LoggerFactory
//import org.springframework.cloud.gateway.filter.GatewayFilterChain
//import org.springframework.cloud.gateway.filter.GlobalFilter
//import org.springframework.core.Ordered
//import org.springframework.stereotype.Component
//import org.springframework.web.server.ServerWebExchange
//import reactor.core.publisher.Mono
//
//
//@Component
//class MyPostFilter : GlobalFilter, Ordered {
//
//    val logger = LoggerFactory.getLogger(MyPostFilter::class.java)
//
//
//    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
//        return chain.filter(exchange).then(Mono.fromRunnable { logger.info("My last post filter is executed") })
//    }
//
//    override fun getOrder(): Int {
//        return 0
//    }
//}