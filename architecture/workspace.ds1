workspace "catalog-manager" {

    model {

        user = person "User"
        
        userAdmin = person "User_Admin"

        ecommerce = softwareSystem "E-commerce" {

            auth = container "Auth Service"
            auth_psql_Db = container "Auth Postgres Database"

            orders = container "Order Service"
            order_psql_Db = container "Order Postgres Database"

            batch = container "Batch Service"
            batch_psql_Db = container "Batch Postgres Database"
            batch_mongo_Db = container "Batch Mongo Database"

            stock = container "Stock Service"
            stock_psql_Db = container "Stock Postgres Database"

        }

        rabbitmq = softwareSystem "RabbitMQ" "Message broker for asynchronous events" {

            orderCreatedExchange = container "order.created.exchange" 
            orderCreatedQueue = container "order.created.queue" 

            productCreatedExchange = container "product.created.exchange"
            productCreatedQueue = container "product.created.queue"

            stockDecrementExchange = container "stock.decrement.exchange"
            stockDecrementQueue = container "stock.decrement.queue"

            orderResponseExchange = container "order.response.exchange"
            orderResponseQueue = container "order.response.queue"

            productStockExchange = container "product.stock.exchange"
            productStockQueue = container "product.stock.queue"

        }

        user -> auth "Authenticates"
        
        userAdmin -> auth "Authenticates"

        auth -> auth_psql_Db "Reads and writes Users/Roles"
        
        user -> orders "Creates orders using JWT"
        
        userAdmin -> orders "Create product using JWT (ROLE_ADMIN)"


        
        orders -> order_psql_Db "Reads, writes products and order, decrement Product quantity"

        orders -> orderCreatedExchange "Converts OrderEvent into json and publishes using RabbitTemplate"

        orderCreatedExchange -> orderCreatedQueue "Binding / Routing Key: order.created.routing.key"

        orderCreatedQueue -> batch "Consumes OrderEvent message via @RabbitListener"

        orders -> productCreatedExchange "Converts ProductEvent into json and publishes using RabbitTemplate"

        productCreatedExchange -> productCreatedQueue "Binding / Routing Key: product.created.routing.key"

        productCreatedQueue -> stock "Consumes ProductEvent message via @RabbitListener"



        batch -> batch_psql_Db "Reads and writes"

        batch -> batch_mongo_Db "Reads and writes"
        
        batch -> stockDecrementExchange "Converts OrderEvent completedEvent to JSON and publishes using RabbitTemplate"

        stockDecrementExchange -> stockDecrementQueue "Binding / Routing Key: stock.decrement.routing.key"

        stockDecrementQueue -> stock "Consumes StockDecrementEvent message via @RabbitListener"



        stock -> stock_psql_Db "Reads and writes products"

        stock -> productStockExchange "Converts List<ProductStockSyncDTO> to JSON and publishes using RabbitTemplate"

        productStockExchange -> productStockQueue "Binding / Routing key: product.stock.routing.key"

        productStockQueue -> orders "Consumes ProductStockSyncDTO message via @RabbitListener"

        stock -> orderResponseExchange "Converts OrderEvent to JSON and publishes using RabbitTemplate"

        orderResponseExchange -> orderResponseQueue "Binding / Routing key: order.response.routing.key"

        orderResponseQueue -> orders "Consumes OrderEvent message via @RabbitListener"

    }

    views {

        systemContext ecommerce {
            include *
            autolayout lr
        }

        container ecommerce {
            include *
            autolayout lr
        }

        container ecommerce "rabbitmq-messaging" {
            include orders
            include orderCreatedExchange
            include orderCreatedQueue
            include productCreatedExchange
            include productCreatedQueue
            include batch
            include stockDecrementExchange
            include stockDecrementQueue
            include stock
            include orderResponseExchange
            include orderResponseQueue 
            include productStockExchange
            include productStockQueue
            autolayout lr
        }
    }
}