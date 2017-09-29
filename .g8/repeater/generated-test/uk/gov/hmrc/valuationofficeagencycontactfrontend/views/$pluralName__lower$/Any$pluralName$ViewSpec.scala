package uk.gov.hmrc.valuationofficeagencycontactfrontend.views.$pluralName;format="lower"$

import play.api.data.Form
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.$pluralName;format="lower"$.routes
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.BooleanForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.YesNoViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.$pluralName;format="lower"$.any$pluralName$

class Any$pluralName$ViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "$pluralName;format="decap"$.any$pluralName$"

  def createView = () => any$pluralName$(frontendAppConfig, BooleanForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Boolean]) => any$pluralName$(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "Any$pluralName$ view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.Any$pluralName$Controller.onSubmit(NormalMode).url)
  }
}
