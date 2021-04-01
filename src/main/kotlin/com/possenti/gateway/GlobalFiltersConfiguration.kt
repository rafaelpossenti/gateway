//package com.possenti.gateway
//
//import org.slf4j.LoggerFactory
//import org.springframework.cloud.gateway.filter.GatewayFilterChain
//import org.springframework.cloud.gateway.filter.GlobalFilter
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.core.annotation.Order
//import org.springframework.web.server.ServerWebExchange
//import reactor.core.publisher.Mono
//
//
//@Configuration
//class GlobalFiltersConfiguration {
//    val logger = LoggerFactory.getLogger(GlobalFiltersConfiguration::class.java)
//
//    @Order(1)
//    @Bean
//    fun secondPreFilter(): GlobalFilter {
//        return GlobalFilter { exchange: ServerWebExchange?, chain: GatewayFilterChain ->
//            logger.info("My second global pre-filter is executed...")
//            chain.filter(exchange).then(Mono.fromRunnable { logger.info("Third post-filter executed...") })
//        }
//    }
//
//    @Order(2)
//    @Bean
//    fun thirdPreFilter(): GlobalFilter {
//        return GlobalFilter { exchange: ServerWebExchange?, chain: GatewayFilterChain ->
//            logger.info("My third global pre-filter is executed...")
//            chain.filter(exchange).then(Mono.fromRunnable { logger.info("My second post-filter is executed...") })
//        }
//    }
//
//    @Order(3)
//    @Bean
//    fun fourthPreFilter(): GlobalFilter {
//        return GlobalFilter { exchange: ServerWebExchange?, chain: GatewayFilterChain ->
//            logger.info("My fourth global pre-filter is executed...")
//            chain.filter(exchange).then(Mono.fromRunnable { logger.info("My first post-filter is executed") })
//        }
//    }
//}