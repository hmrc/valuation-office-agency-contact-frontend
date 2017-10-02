package uk.gov.hmrc.valuationofficeagencycontactfrontend.views.$pluralName;format="lower"$

import play.api.data.Form
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.$pluralName;format="lower"$.routes
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.$className$Form
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{NormalMode, $className$}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.QuestionViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.$pluralName;format="lower"$.add$className$

class Add$className$ViewSpec extends QuestionViewBehaviours[$className$] {

  val messageKeyPrefix = "$pluralName;format="decap"$.add"

  def createView = () => add$className$(frontendAppConfig, $className$Form(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[$className$]) => add$className$(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  override val form = $className$Form()

  "Add $className$ view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithTextFields(createViewUsingForm, messageKeyPrefix, routes.Add$className$Controller.onSubmit(NormalMode).url, "field1", "field2")
  }
}
