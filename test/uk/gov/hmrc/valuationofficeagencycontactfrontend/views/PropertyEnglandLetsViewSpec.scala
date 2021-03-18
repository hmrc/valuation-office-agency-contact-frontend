package uk.gov.hmrc.valuationofficeagencycontactfrontend.views

import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{propertyEnglandLets => property_lets}

class PropertyEnglandLetsViewSpec  extends ViewBehaviours {

  def propertyEnglandLets = app.injector.instanceOf[property_lets]

  def view = () => propertyEnglandLets(frontendAppConfig)(fakeRequest, messages)

  "Property Demolished view" must {
    behave like normalPage(view, "propertyEnglandLets", "title", "heading",
      "p1", "p2.url", "p2.part1", "p2.part2", "subheading", "p3.part1", "p3.url")

    "has a link marked with site.back leading to the Property Demolished Page" in {
      val doc = asDocument(view())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode).url
    }
  }
}
