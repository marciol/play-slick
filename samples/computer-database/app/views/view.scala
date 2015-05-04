package views

import scalatags.Text.TypedTag
import scalatags.Text.all.{html => html2}
import scalatags.Text.all._
import scalatags.Text.tags2.{section, title => title2}
import play.api.i18n._
import models._
import play.twirl.api.{TemplateMagic => tm, Html => H, HtmlFormat => HF}

/**
 * Created by mlf on 02/05/15.
 */
object view {

  def mainLayout(content: Seq[Modifier]) =
    html2(
      head(
        title2("Computers database"),
        link(rel := "stylesheet", `type` :=  "text/css", media := "screen", href := controllers.routes.Assets.at("stylesheets/bootstrap.min.css").url),
        link(rel := "stylesheet", `type` :=  "text/css", media := "screen", href := controllers.routes.Assets.at("stylesheets/main.css").url)
      ),
      body(
        header(`class` := "topbar")(
          h1(`class` := "fill")(
            a(href := controllers.routes.Application.index.url)(
             raw("Play sample application &mdash; Computer database")
            )
          )
        ),
        section(id := "main")(content)
      )
    )

  def list(currentPage: Page[(Computer, Company)], currentOrderBy: Int, currentFilter: String)(implicit flash: play.api.mvc.Flash, messages: Messages) = {

    def link(newPage: Int, newOrderBy: Option[Int] = None) = {
      controllers.routes.Application.list(newPage, newOrderBy.map { orderBy =>
        if(orderBy == scala.math.abs(currentOrderBy)) -currentOrderBy else orderBy
      }.getOrElse(currentOrderBy), currentFilter).url
    }

    def header(orderBy: Int, title: String) = {
      def headerClass = {
        val col = s"col$orderBy"
        val header = if (scala.math.abs(currentOrderBy) == orderBy) "header" else ""
        val headerSort = if (currentOrderBy < 0) "headerSortDown" else "headerSortUp"
        col + header + headerSort
      }

      th(`class` := headerClass)(
        a(href := link(0, Some(orderBy)), title)
      )
    }

    mainLayout(
      Seq(
        h1(
          Messages("computers.list.title", currentPage.total)
        ),

        flash.get("success").map { message =>
          div(`class` := "alert-message warning")(
            strong("Done!"),
            message
          )
        },

        div(id := "actions")(
          form(action := controllers.routes.Application.list().url, method := "GET")(
            input(`type` := "search", id := "searchbox", name := "f", value := currentFilter, placeholder := "Filter by computer name..."),
            input(`type` := "submit", id := "searchsubmit", value := "Filter by name", `class` := "btn primary")
          ),

          a(`class` := "btn success", id := "add", href := controllers.routes.Application.create.url)(
            "Add a new computer"
          )
        ),

        Option(currentPage.items).filterNot(_.isEmpty).map[Modifier] { computers: Seq[(Computer, Company)] =>
          Seq(
            table(`class` := "computers zebra-striped")(
              thead(
                tr(
                  header(2, "Computer name"),
                  header(3, "Introduced"),
                  header(4, "Discontinued"),
                  header(5, "Company")
                )
              ),

              tbody(
                computers.map {
                  case (computer, company) => {
                    val introduced = computer.introduced map { d =>  tm.richDate(d).format("dd MMM yyyy") } getOrElse ""
                    val discontinued = computer.discontinued.map { d => tm.richDate(d).format("dd MMM yyyy") } getOrElse ""
                    tr(
                      td(
                        a(href := controllers.routes.Application.edit(computer.id.get).url)(
                          computer.name
                        )
                      ),
                      td(
                        em(introduced)
                      ),
                      td(
                        em(discontinued)
                      ),
                      td(company.name)
                    )
                  }
                }
              )
            ),

            div(id := "pagination", `class` := "pagination")(
              ul(
                currentPage.prev.map { page =>
                  li(`class` := "prev")(
                    a(href := link(page))(
                      raw("&larr; Previous")
                    )
                  )
                } getOrElse li(`class` := "prev disabled")(
                  a(
                    raw("&larr; Previous")
                  )
                ),

                li(`class` := "current")(
                  a("Displaying %d to %d of of %d".format(currentPage.offset + 1, currentPage.offset + computers.size, currentPage.total))
                ),

                currentPage.next.map { page =>
                  li(`class` := "next")(
                    a(href := link(page))(
                      raw("Next &rarr;")
                    )
                  )
                } getOrElse li(`class` := "next disabled")(
                  a(
                    raw("Next &rarr;")
                  )
                )
              )
            )
          )
        } getOrElse
          div(`class` := "well")(
            em("Nothing to display")
          )
      )
    )
  }
}

