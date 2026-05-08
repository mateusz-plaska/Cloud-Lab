rootProject.name = "outbound-system"

include("common")

val services = listOf(
    "order-gateway-service",
    "packing-service",
    "picking-service",
    "reservation-service",
    "shipping-service"
)

for (service in services) {
    val serviceDir = File(rootDir, service)
    if (serviceDir.exists()) {
        include(service)
    }
}
