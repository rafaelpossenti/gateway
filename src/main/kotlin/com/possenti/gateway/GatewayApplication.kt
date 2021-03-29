package com.possenti.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class GatewayApplication

//@Bean
//fun myRoutes(builder: RouteLocatorBuilder): RouteLocator? {
//	return builder.routes().build()
//}

fun main(args: Array<String>) {
	runApplication<GatewayApplication>(*args)
}
