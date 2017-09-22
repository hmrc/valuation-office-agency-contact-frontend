#!/bin/bash

if grep -Fxp EnquiryCategory applied
then
    echo "Migration EnquiryCategory has already been applied, exiting"
    exit 1
fi

echo "Applying migration EnquiryCategory"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.Routes
echo "GET        /enquiryCategory               uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.EnquiryCategoryController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.Routes
echo "POST       /enquiryCategory               uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.EnquiryCategoryController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.Routes

echo "GET        /changeEnquiryCategory               uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.EnquiryCategoryController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.Routes
echo "POST       /changeEnquiryCategory               uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.EnquiryCategoryController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.Routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "enquiryCategory.title = enquiryCategory" >> ../conf/messages.en
echo "enquiryCategory.heading = enquiryCategory" >> ../conf/messages.en
echo "enquiryCategory.option1 = enquiryCategory" Option 1 >> ../conf/messages.en
echo "enquiryCategory.option2 = enquiryCategory" Option 2 >> ../conf/messages.en
echo "enquiryCategory.checkYourAnswersLabel = enquiryCategory" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def enquiryCategory: Option[String] = cacheMap.getEntry[String](EnquiryCategoryId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def enquiryCategory: Option[AnswerRow] = userAnswers.enquiryCategory map {";\
     print "    x => AnswerRow(\"enquiryCategory.checkYourAnswersLabel\", s\"enquiryCategory.$x\", true, routes.EnquiryCategoryController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Registering this migration as completed"
echo "EnquiryCategory" >> applied
