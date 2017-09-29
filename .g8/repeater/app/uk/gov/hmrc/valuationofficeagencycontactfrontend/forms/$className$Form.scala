package uk.gov.hmrc.valuationofficeagencycontactfrontend.forms

import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.$className$

object $className$Form {

  def apply(): Form[$className$] = Form(
    mapping(
      "field1" -> nonEmptyText,
      "field2" -> nonEmptyText
    )($className$.apply)($className$.unapply)
  )
}
