package inc.encora.inventory_manager.common.docs;

public class ApiExamples {
    
    public static final String PRODUCT_SUCCESS_RESPONSE = """
            {
              "status": "Ok",
              "statusCode": 200,
              "message": "Products retrieved successfully",
              "data": {
                "content": [
                  {
                    "id": "550e8400-e29b-41d4-a716-446655440000",
                    "name": "Laptop Pro (TechCo)",
                    "category": "Electronics",
                    "unitPrice": 1299.99,
                    "expirationDate": "2026-12-31",
                    "quantityInStock": 15,
                    "createdAt": "2025-06-25",
                    "updatedAt": "2025-06-25"
                  }
                ],
                "pageable": {
                  "pageNumber": 0,
                  "pageSize": 10
                },
                "totalElements": 20,
                "totalPages": 2
              },
              "error": null
            }
            """;

    public static final String PRODUCT_CREATED_RESPONSE = """
            {
              "status": "Ok",
              "statusCode": 201,
              "message": "Product saved successfully",
              "data": {
                "id": "550e8400-e29b-41d4-a716-446655440001",
                "name": "Gaming Mouse X (GigaGear)",
                "category": "Peripherals",
                "unitPrice": 79.99,
                "expirationDate": "2027-03-15",
                "quantityInStock": 50,
                "createdAt": "2025-06-25",
                "updatedAt": "2025-06-25"
              },
              "error": null
            }
            """;

    public static final String VALIDATION_ERROR_RESPONSE = """
            {
              "status": "Bad Request",
              "statusCode": 400,
              "message": "Validation Errors",
              "data": null,
              "error": {
                "name": "Product name is required",
                "unitPrice": "Unit price cannot be 0 or negative"
              }
            }
            """;

    public static final String NOT_FOUND_RESPONSE = """
            {
              "status": "Not Found",
              "statusCode": 404,
              "message": "Product Not Found",
              "data": null,
              "error": {
                "details": "Product not found"
              }
            }
            """;

    public static final String DELETE_SUCCESS_RESPONSE = """
            {
              "status": "Ok",
              "statusCode": 200,
              "message": "Product deleted successfully",
              "data": null,
              "error": null
            }
            """;

    public static final String STOCK_SUCCESS_RESPONSE = """
            {
              "status": "Ok",
              "statusCode": 200,
              "message": "Product stock updated successfully",
              "data": null,
              "error": null
            }
            """;

    public static final String CATEGORIES_RESPONSE = """
            {
              "status": "Ok",
              "statusCode": 200,
              "message": "Categories retrieved successfully",
              "data": [
                "Electronics",
                "Peripherals",
                "Computing",
                "Mobile Devices"
              ],
              "error": null
            }
            """;

    public static final String METRICS_RESPONSE = """
            {
              "status": "Ok",
              "statusCode": 200,
              "message": "Inventory metrics retrieved successfully",
              "data": [
                {
                  "category": "Electronics",
                  "totalProductsInStock": 150,
                  "totalValueInStock": 15750.50,
                  "averagePriceInStock": 105.00
                },
                {
                  "category": "Overall",
                  "totalProductsInStock": 1250,
                  "totalValueInStock": 87450.25,
                  "averagePriceInStock": 69.96
                }
              ],
              "error": null
            }
            """;
}
