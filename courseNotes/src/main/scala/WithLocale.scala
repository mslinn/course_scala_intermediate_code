import java.util.{Date, Locale}
import java.text.{NumberFormat, DateFormat}

object WithLocale extends App {
  def formatDateTime(date: Date)(implicit dateFormat: DateFormat): String = dateFormat.format(date)

  def formatNumber(number: BigDecimal)(implicit numberFormat: NumberFormat): String = numberFormat.format(number)

  def withLocale(locale: Locale)(body: Locale => String): String = body(locale)

  def result(date: Date, number: BigDecimal)(implicit locale: Locale): String = {
    implicit val dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, locale)
    implicit val numberFormat = NumberFormat.getNumberInstance(locale)
    val currencyCode = numberFormat.getCurrency.getCurrencyCode
    s"""${locale.getDisplayCountry}; currency: $currencyCode; """ +
      s"""date/time: ${formatDateTime(date)}; large number: ${formatNumber(number)}"""
  }

  val resultUS = withLocale(Locale.US) { implicit locale =>
    result(new Date, 123456789)
  }

  val resultFrance = withLocale(Locale.FRANCE) { implicit locale =>
    result(new Date, 987654321)
  }

  println(resultUS)
  println(resultFrance)
}
