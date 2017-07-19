package demo

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class BootStrap {

    def init = { servletContext ->
        Book.saveAll(
                new Book(name: 'Grails 3: A Practical Guide to Application Development'),
                new Book(name: 'Falando de Grails'),
                new Book(name: 'The Definitive Guide to Grails 2'),
                new Book(name: 'Grails in Action')
        )
    }
    def destroy = {
    }
}
