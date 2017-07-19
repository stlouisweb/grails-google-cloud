package demo


import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.NOT_FOUND
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import grails.transaction.Transactional


@SuppressWarnings('LineLength')
@CompileStatic
class BookController {

    static allowedMethods = [
            index              : 'GET',
            show               : 'GET',
            edit               : 'GET',
            create             : 'GET',
            editFeaturedImage  : 'GET',
            save               : 'POST',
            update             : 'PUT',
            uploadFeaturedImage: 'POST',
            delete             : 'DELETE',
    ]

    UploadBookFeaturedImageService uploadBookFeaturedImageService

    BookGormService bookGormService

    @CompileDynamic
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def (l, total) = bookGormService.list(params)
        respond l, model: [bookCount: total]
    }

    @Transactional(readOnly = true)
    def show(Book book) {
        respond book
    }

    @SuppressWarnings(['FactoryMethodName', 'GrailsMassAssignment'])
    @Transactional(readOnly = true)
    def create() {
        respond new Book(params)
    }

    @Transactional(readOnly = true)
    def edit(Book book) {
        respond book
    }

    @CompileDynamic
    def save(NameCommand cmd) {
        if (cmd == null) {
            notFound()
            return
        }

        if (cmd.hasErrors()) {
            respond cmd.errors, model: [book: cmd], view: 'create'
            return
        }

        def book = bookGormService.save(cmd)

        if (book == null) {
            notFound()
            return
        }

        if (book.hasErrors()) {
            respond book.errors, model: [book: book], view: 'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'book.label', default: 'Book'), book.id])
                redirect book
            }
            '*' { respond book, [status: CREATED] }
        }
    }

    @CompileDynamic
    def update(NameUpdateCommand cmd) {
        if (cmd == null) {
            notFound()
            return
        }

        if (cmd.hasErrors()) {
            respond cmd.errors, model: [book: cmd], view: 'edit'
            return
        }

        def book = bookGormService.update(cmd)

        if (book == null) {
            notFound()
            return
        }

        if (book.hasErrors()) {
            respond book.errors, model: [book: book], view: 'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'book.label', default: 'Book'), book.id])
                redirect book
            }
            '*' { respond book, [status: OK] }
        }
    }

    @CompileDynamic
    def delete() {

        Long bookId = params.long('id')

        if (!bookId) {
            notFound()
            return
        }

        bookGormService.deleteById(bookId)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'book.label', default: 'Book'), bookId])
                redirect action: 'index', method: 'GET'
            }
            '*' { render status: NO_CONTENT }
        }
    }

    @Transactional(readOnly = true)
    def editFeaturedImage(Book book) {
        respond book
    }
    @CompileDynamic
    def uploadFeaturedImage(FeaturedImageCommand cmd) {

        if (cmd.hasErrors()) {
            respond(cmd.errors, model: [book: cmd], view: 'editFeaturedImage')
            return
        }

        def book = uploadBookFeaturedImageService.uploadFeaturedImage(cmd)
        if (book == null) {
            notFound()
            return
        }

        if (book.hasErrors()) {
            respond(book.errors, model: [book: book], view: 'editFeaturedImage')
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'book.label', default: 'Book'), book.id])
                redirect book
            }
            '*' { respond book, [status: OK] }
        }
    }
}