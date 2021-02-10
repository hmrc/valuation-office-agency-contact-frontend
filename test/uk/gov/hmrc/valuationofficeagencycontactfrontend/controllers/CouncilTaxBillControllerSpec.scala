package uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers

import play.api.test.Helpers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.MessageControllerComponentsHelpers
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{councilTaxBill => council_tax_bill}


class CouncilTaxBillControllerSpec extends ControllerSpecBase {
  def councilTaxBill = app.injector.instanceOf[council_tax_bill]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new CouncilTaxBillController(frontendAppConfig, messagesApi, dataRetrievalAction, new DataRequiredActionImpl(ec),
      councilTaxBill, MessageControllerComponentsHelpers.stubMessageControllerComponents)

  "Council Tax Bill Controller" must {
    "return the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)
      contentAsString(result) mustBe councilTaxBill(frontendAppConfig)(fakeRequest, messages).toString
    }
  }
}
