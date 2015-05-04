package filters

import play.api.libs.iteratee.Iteratee
import play.api.mvc._
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * Created by mlf on 02/05/15.
 */
class LoggingFilter extends EssentialFilter {
  def apply(nextFilter: EssentialAction) = new EssentialAction {
    override def apply(requestHeader: RequestHeader): Iteratee[Array[Byte], Result] = {
      val startTime = System.currentTimeMillis

      nextFilter(requestHeader).map { result =>
        val endTime = System.currentTimeMillis
        val requestTime = endTime - startTime

        Logger.info(s"${requestHeader.method} ${requestHeader.uri}" +
          s" took ${requestTime}ms and returned ${result.header.status}")

        result.withHeaders("Request-Time" -> requestTime.toString)
      }
    }
  }
}
