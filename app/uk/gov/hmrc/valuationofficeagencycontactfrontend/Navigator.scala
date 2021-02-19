/*
 * Copyright 2021 HM Revenue & Customs
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

  val enquiryDateRouting: UserAnswers => Call = answers => {
    answers.enquiryDate match {
      case Some("yes") => routes.ExistingEnquiryCategoryController.onPageLoad()
      case Some("no") => routes.ExpectedUpdateController.onPageLoad()
      case Some("notKnow") => routes.ExistingEnquiryCategoryController.onPageLoad()
      case (option) => {
        Logger.warn(s"Navigation enquiry date reached with unknown option $option by controller")
        throw new RuntimeException(s"Navigation for enquiry date reached with unknown option $option by controller")
      }
    }
  }

  val propertyAddressRouting: UserAnswers => Call = answers => {
    answers.contactReason match {
      case Some("new_enquiry") => routes.TellUsMoreController.onPageLoad(NormalMode)
      case Some("more_details") => routes.WhatElseController.onPageLoad()
      case Some("update_existing") => routes.AnythingElseTellUsController.onPageLoad()
      case Some(option) => {
        Logger.warn(s"Navigation for contact reason reached with unknown option $option by controller")
        throw new RuntimeException(s"Navigation for contact reason reached with unknown option $option by controller")
      }
    }
  }

  val contactReasonRouting: UserAnswers => Call = answers => {
    answers.contactReason match {
      case Some("new_enquiry") => routes.EnquiryCategoryController.onPageLoad(NormalMode)
      case Some("more_details") => routes.ExistingEnquiryCategoryController.onPageLoad()
      case Some("update_existing") => routes.EnquiryDateController.onPageLoad()
      case Some(option) => {
        Logger.warn(s"Navigation for contact reason reached with unknown option $option by controller")
        throw new RuntimeException(s"Navigation for contact reason reached with unknown option $option by controller")
      }
    }
  }

  val enquiryRouting: UserAnswers => Call = answers => {
    answers.enquiryCategory match {
      case Some("council_tax") => routes.CouncilTaxSmartLinksController.onPageLoad()
      case Some("business_rates") => routes.BusinessRatesSmartLinksController.onPageLoad()
      case Some("housing_benefit") => routes.HousingBenefitsController.onPageLoad()
      case Some("valuations_for_tax") => routes.ValuationForTaxesController.onPageLoad()
      case Some("providing_lettings") => routes.ProvidingLettingsController.onPageLoad()
      case Some("valuation_for_public_body") => routes.ValuationAdviceController.onPageLoad()
      case Some(option) => {
        Logger.warn(s"Navigation for enquiry category reached with unknown option $option by controller")
        throw new RuntimeException(s"Navigation for enquiry category reached with unknown option $option by controller")
      }
      case None => {
        Logger.warn("Navigation for enquiry category reached without selection of enquiry by controller")
        throw new RuntimeException("Navigation for enquiry category reached without selection of enquiry by controller")
      }
    }
  }

  val contactDetailsRouting: UserAnswers => Call = answers => {
    (answers.contactReason, answers.enquiryCategory) match {
      case (Some("more_details"), _) => routes.PropertyAddressController.onPageLoad(NormalMode)
      case (Some("update_existing"), _) => routes.PropertyAddressController.onPageLoad(NormalMode)
      case (_, Some("council_tax")) => routes.PropertyAddressController.onPageLoad(NormalMode)
      case (_, Some("business_rates")) => routes.PropertyAddressController.onPageLoad(NormalMode)
      case (_, Some(sel)) => {
        Logger.warn(s"Navigation for contact details page reached with an unknown selection $sel of enquiry by controller")
        throw new RuntimeException(s"Navigation for contact details page reached unknown selection $sel of enquiry by controller")
      }
      case _ => {
        Logger.warn("Navigation for contact details page reached without selection of enquiry by controller")
        throw new RuntimeException("Navigation for contact details page reached without selection of enquiry by controller")
      }
    }
  }

  val confirmationPageRouting: UserAnswers => Call = answers => {
    answers.contact().fold(msg => {
      Logger.warn(msg)
      throw new RuntimeException(msg)
    }, _ => answers.enquiryCategory.orElse(answers.existingEnquiryCategory) match {
      case Some("council_tax") | Some("business_rates") => routes.ConfirmationController.onPageLoad()
      case Some("housing_allowance") | Some("other") => routes.ConfirmationController.onPageLoad()
      case Some(sel) =>
        Logger.warn(s"Navigation for confirmation page reached with an unknown selection $sel of enquiry by controller")
        throw new RuntimeException(s"Navigation for confirmation page reached unknown selection $sel of enquiry by controller")
      case None =>
        Logger.warn("Navigation for confirmation page reached without selection of enquiry by controller")
        throw new RuntimeException("Navigation for confirmation page reached without selection of enquiry by controller")
    })
  }

  val businessRatesPageRouting: UserAnswers => Call = answers => {
    answers.businessRatesSubcategory match {
      case Some("business_rates_challenge") => routes.BusinessRatesChallengeController.onChallengePageLoad()
      case Some("business_rates_changes") => routes.BusinessRatesChallengeController.onAreaChangePageLoad()
      case Some(_) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case None => {
        Logger.warn(s"Navigation for Business Rates page reached without selection of enquiry by controller ")
        throw new RuntimeException("Unknown exception in Business Page Routing")
      }

    }
  }

  val councilTaxPageRouting: UserAnswers => Call = answers => {
    answers.councilTaxSubcategory match {
      case Some("council_tax_band_too_high") => routes.CouncilTaxBandTooHighController.onPageLoad()
      case Some("council_tax_bill") => routes.CouncilTaxBillController.onPageLoad()
      case Some("council_tax_band_for_new") => routes.CouncilTaxBandForNewController.onPageLoad()
      case Some("council_tax_property_empty") => routes.CouncilTaxPropertyEmptyController.onPageLoad()
      case Some("property_demolished") => routes.PropertyDemolishedController.onPageLoad()
      case Some(_) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case None => {
        Logger.warn(s"Navigation for Council Tax page reached without selection of enquiry by controller ")
        throw new RuntimeException("Unknown exception in Council Tax Routing")
      }
    }
  }

  private val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    ContactReasonId -> contactReasonRouting,
    EnquiryDateId -> enquiryDateRouting,
    ExistingEnquiryCategoryId -> (_ => routes.RefNumberController.onPageLoad()),
    RefNumberId -> (_ => routes.ContactDetailsController.onPageLoad(NormalMode)),
    EnquiryCategoryId -> enquiryRouting,
    CouncilTaxSubcategoryId -> councilTaxPageRouting,
    BusinessRatesSubcategoryId -> businessRatesPageRouting,
    ContactDetailsId -> contactDetailsRouting,
    PropertyAddressId -> propertyAddressRouting,
    WhatElseId -> (_ => routes.CheckYourAnswersController.onPageLoad()),
    TellUsMoreId -> (_ => routes.CheckYourAnswersController.onPageLoad()),
    AnythingElseId -> (_ => routes.CheckYourAnswersController.onPageLoad()),
    CheckYourAnswersId -> confirmationPageRouting,
    CouncilTaxSmartLinksId -> (_ => routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode)),
    BusinessRatesSmartLinksId -> (_ => routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode))
  )

  private val editRouteMap: Map[Identifier, UserAnswers => Call] = Map()

  def nextPage(id: Identifier, mode: Mode): UserAnswers => Call = mode match {
    case NormalMode =>
      routeMap.getOrElse(id, _ => routes.EnquiryCategoryController.onPageLoad(NormalMode))
    case CheckMode =>
      editRouteMap.getOrElse(id, _ => routes.CheckYourAnswersController.onPageLoad())
  }
}
