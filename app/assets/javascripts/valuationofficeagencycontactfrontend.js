/*
 * Copyright 2018 HM Revenue & Customs
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
$(document).ready(function() {

  // =====================================================
  // Initialise show-hide-content
  // Toggles additional content based on radio/checkbox input state
  // =====================================================
      var showHideContent = new GOVUK.ShowHideContent()
      showHideContent.init()

  // =====================================================
  // Handle number inputs
  // =====================================================
    numberInputs();

  // =====================================================
  // Adds data-focuses attribute to all containers of inputs listed in an error summary
  // This allows validatorFocus to bring viewport to correct scroll point
  // =====================================================
      function assignFocus () {
          var counter = 0;
          $('.error-summary-list a').each(function(){
              var linkhash = $(this).attr("href").split('#')[1];
              $('#' + linkhash).parents('.form-field, .form-group').first().attr('id', 'f-' + counter);
              $(this).attr('data-focuses', 'f-' + counter);
              counter++;
          });
      }
      assignFocus();

      function beforePrintCall(){
          if($('.no-details').length > 0){
              // store current focussed element to return focus to later
              var fe = document.activeElement;
              // store scroll position
              var scrollPos = window.pageYOffset;
              $('details').not('.open').each(function(){
                  $(this).addClass('print--open');
                  $(this).find('summary').trigger('click');
              });
              // blur focus off current element in case original cannot take focus back
              $(document.activeElement).blur();
              // return focus if possible
              $(fe).focus();
              // return to scroll pos
              window.scrollTo(0,scrollPos);
          } else {
              $('details').attr("open","open").addClass('print--open');
          }
          $('details.print--open').find('summary').addClass('heading-medium');
      }

      function afterPrintCall(){
          $('details.print--open').find('summary').removeClass('heading-medium');
          if($('.no-details').length > 0){
              // store current focussed element to return focus to later
              var fe = document.activeElement;
              // store scroll position
              var scrollPos = window.pageYOffset;
              $('details.print--open').each(function(){
                  $(this).removeClass('print--open');
                  $(this).find('summary').trigger('click');
              });
              // blur focus off current element in case original cannot take focus back
              $(document.activeElement).blur();
              // return focus if possible
              $(fe).focus();
              // return to scroll pos
              window.scrollTo(0,scrollPos);
          } else {
              $('details.print--open').removeAttr("open").removeClass('print--open');
          }
      }

      //Chrome
      if(typeof window.matchMedia != 'undefined'){
          var mediaQueryList = window.matchMedia('print');
          mediaQueryList.addListener(function(mql) {
              if (mql.matches) {
                  beforePrintCall();
              };
              if (!mql.matches) {
                  afterPrintCall();
              };
          });
      }

      //Firefox and IE (above does not work)
      window.onbeforeprint = function(){
          beforePrintCall();
      }
      window.onafterprint = function(){
          afterPrintCall();
      }
  });


  function numberInputs() {
      // =====================================================
      // Set currency fields to number inputs on touch devices
      // this ensures on-screen keyboards display the correct style
      // don't do this for FF as it has issues with trailing zeroes
      // =====================================================
      if($('html.touchevents').length > 0 && window.navigator.userAgent.indexOf("Firefox") == -1){
          $('[data-type="currency"] > input[type="text"], [data-type="percentage"] > input[type="text"]').each(function(){
            $(this).attr('type', 'number');
            $(this).attr('step', 'any'); 
            $(this).attr('min', '0');
          });
      }

      // =====================================================
      // Disable mouse wheel and arrow keys (38,40) for number inputs to prevent mis-entry
      // also disable commas (188) as they will silently invalidate entry on Safari 10.0.3 and IE11
      // =====================================================
      $("form").on("focus", "input[type=number]", function(e) {
          $(this).on('wheel', function(e) {
              e.preventDefault();
          });
      });
      $("form").on("blur", "input[type=number]", function(e) {
          $(this).off('wheel');
      });
      $("form").on("keydown", "input[type=number]", function(e) {
          if ( e.which == 38 || e.which == 40 || e.which == 188 )
              e.preventDefault();
      });
  }

  // =====================================================
  // Track radio options
  // =====================================================
  trackRadios();
  function trackRadios(){
      $("[data-ga-track-id] input:radio").change(function(){
          var val = $("[data-ga-track-id] input:radio:checked").val()
          var id = $("[data-ga-track-id]").attr('data-ga-track-id');
          $("[data-ga-track-id]").closest('form').find('#submit').attr("onclick", 'ga("send", "event", "'+id+'", "click", "'+val+'")')
      });

  }
