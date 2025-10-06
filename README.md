# Spring Boot E-Commerce CRUD API

A RESTful API for e-commerce product management built with Spring Boot and H2 in-memory database.

## 🚀 Features

- **Complete CRUD Operations** - Create, Read, Update, Delete products
- **RESTful API Design** - Standard HTTP methods and status codes
- **H2 In-Memory Database** - Fast setup for development and testing
- **Spring Data JPA** - Simplified database operations
- **Postman Ready** - Fully tested API endpoints

## 🛠️ Tech Stack

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **H2 Database**
- **Maven**

## 📋 API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/products` | Get all products |
| GET | `/api/products/{id}` | Get product by ID |
| POST | `/api/products` | Create new product |
| PUT | `/api/products/{id}` | Update product |
| DELETE | `/api/products/{id}` | Delete product |

## 🏃‍♂️ Quick Start

```bash
# Clone repository
git clone https://github.com/kandpalpk/spring-boot-crud-ecommerce.git

# Navigate to project
cd spring-boot-crud-ecommerce

# Run application
mvn spring-boot:run
```

**Access Points:**
- API: `http://localhost:8080/api/products`
- H2 Console: `http://localhost:8080/h2-console`

## 📝 Sample Request

**POST** `/api/products`
```json
{
  "name": "iPhone 15",
  "brand": "Apple",
  "description": "Latest smartphone",
  "price": 999.99,
  "category": "Electronics",
  "quantity": 50,
  "productAvailable": true
}
```

## 🧪 Testing

Import the Postman collection and test all CRUD operations. The application includes comprehensive error handling and validation.

## 👨‍💻 Author

**Prakhar Kandpal** - [@kandpalpk](https://github.com/kandpalpk)

---
⭐ **Star this repo if you found it helpful!**