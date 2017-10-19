/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.valuationofficeagencycontactfrontend

import javax.inject.{Inject, Singleton}

import play.api.mvc.Call
import uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{CheckMode, Mode, NormalMode}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers
import play.api.Logger

@Singleton
class Navigator @Inject()() {

  val enquiryRouting: UserAnswers => Call = answers => {
    answers.enquiryCategory match {
      case Some("council_tax") => routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode)
      case Some("business_rates") => routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode)
      case Some("housing_benefit") => routes.HousingBenefitsController.onPageLoad()
      case Some("valuations_for_tax") => routes.ValuationForTaxesController.onPageLoad()
      case Some("providing_lettings") => routes.ProvidingLettingsController.onPageLoad()
      case Some("valuation_for_public_body") => routes.ValuationAdviceController.onPageLoad()
      case Some(_) => routes.StaticPagePlaceholderController.onPageLoad()
      case None => {
        Logger.warn("Navigation for enquiry category reached without selection of enquiry by controller")
        throw new RuntimeException("Navigation for enquiry category reached without selection of enquiry by controller")
      }
    }
  }

  val businessSubcategoryRouting: UserAnswers => Call = answers => {
    answers.businessRatesSubcategory match {
      case Some("business_rates_update_details") => routes.CheckAndChallengeController.onPageLoad()
      case Some("business_rates_challenge") => routes.CheckAndChallengeController.onPageLoad()
      case Some(_) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case None => {
        Logger.warn("Navigation for business rates subcategory reached without selection of enquiry by controller")
        throw new RuntimeException("Navigation for business rates subcategory reached without selection of enquiry by controller")
      }
    }
  }

  val contactDetailsRouting: UserAnswers => Call = answers => {
    answers.enquiryCategory match {
      case Some("council_tax") => routes.PropertyAddressController.onPageLoad(NormalMode)
      case Some("business_rates") => routes.PropertyAddressController.onPageLoad(NormalMode)
      case Some(sel) => {
        Logger.warn(s"Navigation for contact details page reached with an unknown selection $sel of enquiry by controller")
        throw new RuntimeException(s"Navigation for contact details page reached unknown selection $sel of enquiry by controller")
      }
      case None => {
        Logger.warn("Navigation for contact details page reached without selection of enquiry by controller")
        throw new RuntimeException("Navigation for contact details page reached without selection of enquiry by controller")
      }
    }
  }

  val confirmationPageRouting: UserAnswers => Call = answers => {
    answers.contact match {
      case Left(msg) => {
        Logger.warn(msg)
        throw new RuntimeException(msg)
      }
      case Right(addr) => answers.enquiryCategory match {
        case Some("council_tax") | Some("business_rates") => routes.ConfirmationController.onPageLoad()
        case Some(sel) => {
          Logger.warn(s"Navigation for confirmation page reached with an unknown selection $sel of enquiry by controller")
          throw new RuntimeException(s"Navigation for confirmation page reached unknown selection $sel of enquiry by controller")
        }
        case None => {
          Logger.warn("Navigation for confirmation page reached without selection of enquiry by controller")
          throw new RuntimeException("Navigation for confirmation page reached without selection of enquiry by controller")
        }
      }
      case _ => {
        Logger.warn("Unknown exception in Confirmation Page Routing")
        throw new RuntimeException("Unknown exception in Confirmation Page Routing")
      }
    }
  }

  private val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    EnquiryCategoryId -> enquiryRouting,
    CouncilTaxSubcategoryId -> (_ => routes.ContactDetailsController.onPageLoad(NormalMode)),
    BusinessRatesSubcategoryId -> businessSubcategoryRouting,
    ContactDetailsId -> contactDetailsRouting,
    PropertyAddressId -> (_ => routes.TellUsMoreController.onPageLoad(NormalMode)),
    TellUsMoreId -> (_ => routes.CheckYourAnswersController.onPageLoad()),
    CheckYourAnswersId -> confirmationPageRouting
  )

  private val editRouteMap: Map[Identifier, UserAnswers => Call] = Map()

  def nextPage(id: Identifier, mode: Mode): UserAnswers => Call = mode match {
    case NormalMode =>
      routeMap.getOrElse(id, _ => routes.IndexController.onPageLoad())
    case CheckMode =>
      editRouteMap.getOrElse(id, _ => routes.CheckYourAnswersController.onPageLoad())
  }
}
