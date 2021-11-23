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

  private val log = Logger(this.getClass)

  private val enquiryDateRouting: UserAnswers => Call = answers => {
    answers.enquiryDate match {
      case Some("yes") => routes.ExistingEnquiryCategoryController.onPageLoad()
      case Some("no") => routes.ExpectedUpdateController.onPageLoad()
      case Some("notKnow") => routes.ExistingEnquiryCategoryController.onPageLoad()
      case (option) => {
        log.warn(s"Navigation enquiry date reached with unknown option $option by controller")
        throw new RuntimeException(s"Navigation for enquiry date reached with unknown option $option by controller")
      }
    }
  }

  private val propertyAddressRouting: UserAnswers => Call = answers => {
    (answers.contactReason, answers.councilTaxSubcategory, answers.businessRatesSubcategory, answers.fairRentEnquiryEnquiry) match {
      case (Some("new_enquiry"), Some("council_tax_property_poor_repair"), _, _) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), Some("council_tax_business_uses"), _, _) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), Some("council_tax_area_change"), _, _) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), Some("council_tax_other"), _, _) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), Some("council_tax_annexe"), _, _) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), Some("council_tax_bill"), _, _) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), Some("council_tax_band_too_high"), _, _) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), Some("council_tax_band_for_new"), _, _) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), Some("council_tax_property_empty"), _, _) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), Some("council_tax_property_split_merge"), _, _) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), Some("council_tax_property_demolished"), _, _) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), _, Some("business_rates_change_valuation"), _) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), _, Some("business_rates_bill"), _) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), _, Some("business_rates_changes"), _) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), _, Some("business_rates_from_home"), _) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), _, Some("business_rates_other"), _) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), _, Some("business_rates_not_used"), _) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), _, Some("business_rates_self_catering"), _) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), _, Some("business_rates_property_empty"), _) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), _, Some("business_rates_valuation"), _) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), _, Some("business_rates_demolished"), _) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), _, _, Some("submit_new_application")) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), _, _, Some("check_fair_rent_register")) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), _, _, Some("other_request")) => routes.CheckYourAnswersController.onPageLoad
      case (Some("new_enquiry"), _, _, _) => routes.TellUsMoreController.onPageLoad(NormalMode)
      case (Some("more_details"), _, _, _) => routes.WhatElseController.onPageLoad()
      case (Some("update_existing"), _, _, _) => routes.AnythingElseTellUsController.onPageLoad()
      case (Some(option), _, _, _) => {
        log.warn(s"Navigation for contact reason reached with unknown option $option by controller")
        throw new RuntimeException(s"Navigation for contact reason reached with unknown option $option by controller")
      }
    }
  }

  private val contactReasonRouting: UserAnswers => Call = answers => {
    answers.contactReason match {
      case Some("new_enquiry") => routes.EnquiryCategoryController.onPageLoad(NormalMode)
      case Some("more_details") => routes.ExistingEnquiryCategoryController.onPageLoad()
      case Some("update_existing") => routes.EnquiryDateController.onPageLoad()
      case Some(option) => {
        log.warn(s"Navigation for contact reason reached with unknown option $option by controller")
        throw new RuntimeException(s"Navigation for contact reason reached with unknown option $option by controller")
      }
    }
  }

  private val enquiryRouting: UserAnswers => Call = answers => {
    answers.enquiryCategory match {
      case Some("council_tax") => routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode)
      case Some("business_rates") => routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode)
      case Some("housing_benefit") => routes.HousingBenefitsController.onPageLoad()
      case Some("fair_rent") => routes.FairRentEnquiryController.onPageLoad()
      case Some("valuations_for_tax") => routes.ValuationForTaxesController.onPageLoad()
      case Some("providing_lettings") => routes.ProvidingLettingsController.onPageLoad()
      case Some("valuation_for_public_body") => routes.ValuationAdviceController.onPageLoad()
      case Some(option) => {
        log.warn(s"Navigation for enquiry category reached with unknown option $option by controller")
        throw new RuntimeException(s"Navigation for enquiry category reached with unknown option $option by controller")
      }
      case None => {
        log.warn("Navigation for enquiry category reached without selection of enquiry by controller")
        throw new RuntimeException("Navigation for enquiry category reached without selection of enquiry by controller")
      }
    }
  }

  private val contactDetailsRouting: UserAnswers => Call = answers => {
    (answers.contactReason, answers.enquiryCategory)match {
      case (Some("more_details"), _) => routes.PropertyAddressController.onPageLoad(NormalMode)
      case (Some("update_existing"), _) => routes.PropertyAddressController.onPageLoad(NormalMode)
      case (_, Some("council_tax")) => routes.PropertyAddressController.onPageLoad(NormalMode)
      case (_, Some("business_rates")) => routes.PropertyAddressController.onPageLoad(NormalMode)
      case (_, Some("fair_rent")) => routes.PropertyAddressController.onPageLoad(NormalMode)
      case (_, Some(sel)) => {
        log.warn(s"Navigation for contact details page reached with an unknown selection $sel of enquiry by controller")
        throw new RuntimeException(s"Navigation for contact details page reached unknown selection $sel of enquiry by controller")
      }
      case _ => {
        log.warn("Navigation for contact details page reached without selection of enquiry by controller")
        throw new RuntimeException("Navigation for contact details page reached without selection of enquiry by controller")
      }
    }
  }

  private val confirmationPageRouting: UserAnswers => Call = answers => {
    import answers._
    contact().fold(msg => {
      log.warn(msg)
      throw new RuntimeException(msg)
    }, _ =>
      (enquiryCategory.isDefined, existingEnquiryCategory.isDefined) match {
        case (true, false) => enquiryRouting(enquiryCategory, routes.ConfirmationController.onPageLoad())
        case (false, true) => enquiryRouting(existingEnquiryCategory, routes.ConfirmationController.onPageLoad)
        case _ => log.warn("Navigation for confirmation page - Enquiry or Existing Enquiry Subcategory not defined")
          throw new RuntimeException("Navigation for confirmation page - Enquiry or Existing Enquiry Subcategory not defined")
      })
  }

  private def enquiryRouting(enquiry: Option[String], confirmationCall: Call): Call = {
    enquiry match {
      case Some("council_tax") | Some("business_rates") => confirmationCall
      case Some("fair_rent") | Some("other") => confirmationCall
      case Some(sel) =>
        log.warn(s"Navigation for confirmation page reached with an unknown selection $sel of enquiry by controller")
        throw new RuntimeException(s"Navigation for confirmation page reached unknown selection $sel of enquiry by controller")
      case None =>
        log.warn("Navigation for confirmation page reached without selection of enquiry by controller")
        throw new RuntimeException("Navigation for confirmation page reached without selection of enquiry by controller")
    }
  }

  private val businessRatesPageRouting: UserAnswers => Call = answers => {
    answers.businessRatesSubcategory match {
      case Some("business_rates_changes") => routes.BusinessRatesChallengeController.onAreaChangePageLoad()
      case Some("business_rates_self_catering") => routes.BusinessRatesSelfCateringController.onPageLoad()
      case Some("business_rates_from_home") => routes.DatePropertyChangedController.onPageLoad()
      case Some("business_rates_change_valuation")  => routes.BusinessRatesSubcategoryController.onChangeValuationPageLoad()
      case Some("business_rates_demolished")  => routes.BusinessRatesSubcategoryController.onDemolishedPageLoad()
      case Some("business_rates_valuation") => routes.BusinessRatesSubcategoryController.onValuationPageLoad()
      case Some("business_rates_property_empty") => routes.PropertyEmptyController.onBusinessRatesPageLoad()
      case Some("business_rates_bill") => routes.BusinessRatesBillController.onPageLoad()
      case Some("business_rates_not_used") => routes.BusinessRatesPropertyController.onPageLoad()
      case Some("business_rates_other") => routes.TellUsMoreController.onPageLoad(NormalMode)
      case Some(_) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case None => {
        log.warn(s"Navigation for Business Rates page reached without selection of enquiry by controller ")
        throw new RuntimeException("Unknown exception in Business Page Routing")
      }

    }
  }

  private val councilTaxPageRouting: UserAnswers => Call = answers => {
    answers.councilTaxSubcategory match {
      case Some("council_tax_band_too_high") => routes.CouncilTaxBandTooHighController.onPageLoad()
      case Some("council_tax_bill") => routes.CouncilTaxBillController.onPageLoad()
      case Some("council_tax_band_for_new") => routes.CouncilTaxBandForNewController.onPageLoad()
      case Some("council_tax_property_empty") => routes.PropertyEmptyController.onPageLoad()
      case Some("council_tax_property_poor_repair") => routes.PropertyWindWaterController.onPageLoad()
      case Some("council_tax_property_split_merge") => routes.PropertySplitMergeController.onPageLoad()
      case Some("council_tax_property_demolished") => routes.PropertyDemolishedController.onPageLoad()
      case Some("council_tax_annexe") => routes.CouncilTaxAnnexeController.onPageLoad()
      case Some("council_tax_business_uses") => routes.DatePropertyChangedController.onPageLoad()
      case Some("council_tax_area_change") => routes.PropertyPermanentChangesController.onPageLoad()
      case Some("council_tax_other") => routes.TellUsMoreController.onPageLoad(NormalMode)
      case Some(_) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case None => {
        log.warn(s"Navigation for Council Tax page reached without selection of enquiry by controller ")
        throw new RuntimeException("Unknown exception in Council Tax Routing")
      }
    }
  }

  private val tellUsMoreRouting: UserAnswers => Call = answers => {
    (answers.councilTaxSubcategory, answers.businessRatesSubcategory, answers.fairRentEnquiryEnquiry) match {
      case (Some("council_tax_property_poor_repair"), _ , _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (Some("council_tax_business_uses"), _ , _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (Some("council_tax_area_change"), _ , _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (Some("council_tax_other"), _ , _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (Some("council_tax_annexe"), _ , _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (Some("council_tax_bill"), _ , _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (Some("council_tax_band_too_high"), _ , _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (Some("council_tax_band_for_new"), _ , _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (Some("council_tax_property_empty"), _ , _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (Some("council_tax_property_split_merge"), _ , _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (Some("council_tax_property_demolished"), _ , _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, Some("business_rates_from_home") , _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, Some("business_rates_bill") , _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, Some("business_rates_changes") , _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, Some("business_rates_change_valuation") , _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, Some("business_rates_property_empty") , _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, Some("business_rates_not_used") , _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, Some("business_rates_self_catering") , _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, Some("business_rates_valuation") , _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, Some("business_rates_demolished") , _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, Some("business_rates_other") , _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, _, Some("submit_new_application")) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, _, Some("check_fair_rent_register")) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, _, Some("other_request")) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case _ => routes.CheckYourAnswersController.onPageLoad
    }
  }

  private val councilTaxAnnexeRouting: UserAnswers => Call = answers => {
    answers.annexeEnquiry match {
      case Some("added") => routes.CouncilTaxAnnexeController.onSelfContainedEnquiryPageLoad()
      case Some("removed") => routes.CouncilTaxAnnexeController.onRemovedPageLoad()
      case _ =>
        log.warn(s"Navigation for annexe without selection of enquiry by controller ")
        throw new RuntimeException("Unknown exception for annexe routing")
    }
  }

  private val annexeSelfContainedRouting: UserAnswers => Call = answers => {
    answers.annexeSelfContainedEnquiry match {
      case Some("yes") => routes.CouncilTaxAnnexeController.onHaveCookingWashingPageLoad()
      case Some("no") => routes.CouncilTaxAnnexeController.onNotSelfContainedPageLoad()
      case _ =>
        log.warn(s"Navigation for is annexe self contained without selection of enquiry by controller ")
        throw new RuntimeException("Unknown exception for is annexe self contained routing")
    }
  }

  private val annexeCookingWashingRouting: UserAnswers => Call = answers => {
    answers.annexeHaveCookingWashing match {
      case Some("yes") => routes.CouncilTaxAnnexeController.onSelfContainedPageLoad()
      case Some("no") => routes.CouncilTaxAnnexeController.onFacilitiesPageLoad()
      case _ =>
        log.warn(s"Navigation for is annexe cooking washing without selection of enquiry by controller ")
        throw new RuntimeException("Unknown exception for is annexe cooking washing routing")
    }
  }

  private val councilTaxBusinessEnquiryRouting: UserAnswers => Call = answers => {
    (answers.councilTaxBusinessEnquiry, answers.businessRatesSubcategory) match {
      case (Some("all_property"), _) => routes.DatePropertyChangedController.onPageLoad()
      case (Some("large_property"), _) => routes.DatePropertyChangedController.onPageLoad()
      case (Some("small_property"), Some("business_rates_from_home")) => routes.CouncilTaxBusinessController.onSmallPartUsedBusinessRatesPageLoad()
      case (Some("small_property"), _) => routes.CouncilTaxBusinessController.onSmallPartUsedPageLoad()
      case _ =>
        log.warn(s"Navigation for is council tax business enquiry reached without selection of enquiry by controller")
        throw new RuntimeException("Unknown exception for is council tax business enquiry routing")
    }
  }

  private val selfCateringPageRouting: UserAnswers => Call = answers => {
    answers.businessRatesSelfCateringEnquiry match {
      case Some("england") => routes.PropertyEnglandLets140DaysController.onPageLoad()
      case Some("wales") => routes.PropertyWalesLets140DaysController.onPageLoad()
      case _ =>
        log.warn(s"Navigation for is business rates self catering enquiry reached without selection of enquiry by controller")
        throw new RuntimeException("Unknown exception for is business rates self catering routing")
    }
  }

  private val propertyEnglandLets140DaysRouting: UserAnswers => Call = answers => {
    answers.propertyEnglandLets140DaysEnquiry match {
      case Some("yes") => routes.BusinessRatesSelfCateringController.onEngLetsPageLoad()
      case Some("no") => routes.PropertyEnglandLets140DaysController.onEngLetsNoActionPageLoad()
      case _ =>
        log.warn(s"Navigation for is business rates property enquiry reached without selection of enquiry by controller")
        throw new RuntimeException("Unknown exception for is business rates self catering routing")
    }
  }

  private val propertyWalesLets140DaysRouting: UserAnswers => Call = answers => {
    answers.propertyWalesLets140DaysEnquiry match {
      case Some("yes") => routes.PropertyWalesLets70DaysController.onPageLoad()
      case Some("no") => routes.PropertyWalesLetsNoActionController.onPageLoad()

      case _ =>
        log.warn(s"Navigation for is 140 day lets property enquiry reached without selection of enquiry by controller")
        throw new RuntimeException("Unknown exception for 140 day lets routing")
    }
  }

  private val propertyWalesLets70DaysRouting: UserAnswers => Call = answers => {
    answers.propertyWalesLets70DaysEnquiry match {
      case Some("yes") => routes.BusinessRatesSelfCateringController.onWalLetsPageLoad()
      case Some("no") => routes.PropertyWalesLetsNoActionController.onPageLoad()

      case _ =>
        log.warn(s"Navigation for is 70 day lets property enquiry reached without selection of enquiry by controller")
        throw new RuntimeException("Unknown exception for 70 day lets routing")
    }
  }

  private val businessRatesPropertyEnquiryRouting: UserAnswers => Call = answers => {
    answers.businessRatesPropertyEnquiry match {
      case Some("england") => routes.BusinessRatesPropertyController.onNonBusinessPageLoad
      case Some("wales") => routes.DatePropertyChangedController.onPageLoad
      case _ =>
        log.warn(s"Navigation for is business rates property enquiry reached without selection of enquiry by controller")
        throw new RuntimeException("Unknown exception for is business rates self catering routing")
    }
  }

  private val FairRentEnquiryRouting: UserAnswers => Call = answers => {
    answers.fairRentEnquiryEnquiry match {
      case Some("submit_new_application") => routes.FairRentEnquiryController.onFairRentEnquiryNew()
      case Some("check_fair_rent_register") => routes.FairRentEnquiryController.onFairRentEnquiryCheck()
      case Some("other_request") => routes.TellUsMoreController.onPageLoad(NormalMode)
      case _ =>
        log.warn(s"Navigation for fair rent enquiry reached without selection of enquiry by controller")
        throw new RuntimeException("Unknown exception for fair rent enquiry routing")
    }
  }

  private val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    ContactReasonId -> contactReasonRouting,
    EnquiryDateId -> enquiryDateRouting,
    ExistingEnquiryCategoryId -> (_ => routes.RefNumberController.onPageLoad),
    RefNumberId -> (_ => routes.ContactDetailsController.onPageLoad(NormalMode)),
    EnquiryCategoryId -> enquiryRouting,
    CouncilTaxSubcategoryId -> councilTaxPageRouting,
    BusinessRatesSubcategoryId -> businessRatesPageRouting,
    ContactDetailsId -> contactDetailsRouting,
    PropertyAddressId -> propertyAddressRouting,
    WhatElseId -> (_ => routes.CheckYourAnswersController.onPageLoad),
    TellUsMoreId -> tellUsMoreRouting,
    AnythingElseId -> (_ => routes.CheckYourAnswersController.onPageLoad),
    CheckYourAnswersId -> confirmationPageRouting,
    BusinessRatesSmartLinksId -> (_ => routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode)),
    DatePropertyChangedId -> (_ => routes.TellUsMoreController.onPageLoad(NormalMode)),
    CouncilTaxAnnexeSelfContainedEnquiryId -> annexeSelfContainedRouting,
    CouncilTaxAnnexeEnquiryId -> councilTaxAnnexeRouting,
    CouncilTaxAnnexeHaveCookingId -> annexeCookingWashingRouting,
    CouncilTaxBusinessEnquiryId -> councilTaxBusinessEnquiryRouting,
    BusinessRatesSelfCateringId -> selfCateringPageRouting,
    BusinessRatesPropertyEnquiryId -> businessRatesPropertyEnquiryRouting,
    PropertyEnglandLets140DaysId -> propertyEnglandLets140DaysRouting,
    PropertyWalesLets140DaysId -> propertyWalesLets140DaysRouting,
    PropertyWalesLets70DaysId -> propertyWalesLets70DaysRouting,
    FairRentEnquiryId -> FairRentEnquiryRouting
  )

  private val editRouteMap: Map[Identifier, UserAnswers => Call] = Map()

  def nextPage(id: Identifier, mode: Mode): UserAnswers => Call = mode match {
    case NormalMode =>
      routeMap.getOrElse(id, _ => routes.EnquiryCategoryController.onPageLoad(NormalMode))
    case CheckMode =>
      editRouteMap.getOrElse(id, _ => routes.CheckYourAnswersController.onPageLoad)
  }
}
