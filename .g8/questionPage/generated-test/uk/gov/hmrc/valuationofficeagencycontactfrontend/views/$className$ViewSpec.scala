package uk.gov.hmrc.valuationofficeagencycontactfrontend.views

import play.api.data.Form
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.$className$Form
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{NormalMode, $className$}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.QuestionViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.$className;format="decap"$

class $className$ViewSpec extends QuestionViewBehaviours[$className$] {

  val messageKeyPrefix = "$className;format="decap"$"

  def createView = () => $className;format="decap"$(frontendAppConfig, $className$Form(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[$className$]) => $className;format="decap"$(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  override val form = $className$Form()

  "$className$ view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(createViewUsingForm, messageKeyPrefix, routes.$className$Controller.onSubmit(NormalMode).url, "field1", "field2")
  }
}
