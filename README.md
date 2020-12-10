# bookstore
Spring boot based bookstore application. The primary objective to develope bookstore is to learn practical test implementation in a spring app based on the test pyramid concepts.

## About the App
Bookstore is a Rest Application which exposes few endpoints as below:
- /books : Get all the books from repository.
- /book/{isbn} : Get a book by given isbn path parameter.
- /book/price/{isbn} : Get a book price by given isbn path parameter.

The bookstore also listens on a Kafka topic (books) and stores the consumed event in bookRepository (in memory h2 database). It expects the event as `{isbn}:{bookTitle} -> 123:Clean Code`. The same book can be fetched by bookstore APIs.

In addition to above, bookstore talks to an external service to fetch the book price. The fetched price for an isbn used to form `book/price/{isbn}` response. In case of price service failures, the book stores returns failure messages.

# Test Pyramid

The principle behind test automation is to get **_fast and accurate_** feedback for any product changes. One way to do that is **_write lots of small and fast unit tests_**. 
Write some more coarse-grained tests and **_very few high-level_** tests that test your application from end to end.

![Test Pyramid](docs/images/test-pyramid.png)

Some useful resources on test pyramid:
- https://martinfowler.com/bliki/TestPyramid.html
- https://testpyramid.com/



