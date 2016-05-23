/*
 * LICENCE : CloudUnit is available under the Affero Gnu Public License GPL V3 : https://www.gnu.org/licenses/agpl-3.0.html
 *     but CloudUnit is licensed too under a standard commercial license.
 *     Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 *     If you are not sure whether the GPL is right for you,
 *     you can always test our software under the GPL and inspect the source code before you contact us
 *     about purchasing a commercial license.
 *
 *     LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 *     or promote products derived from this project without prior written permission from Treeptik.
 *     Products or services derived from this software may not be called "CloudUnit"
 *     nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 *     For any questions, contact us : contact@treeptik.fr
 */

(function () {
  'use strict';

  /**
   * @ngdoc function
   * @name webuiApp.controller:MainCtrl
   * @description
   * # MainCtrl
   * Controller of the webuiApp
   */
  angular
    .module('webuiApp')
    .directive('createApp', CreateApp);

  function CreateApp(){
    return {
      restrict: 'E',
      templateUrl: 'scripts/components/dashboard/dashboard.createApplication.html',
      scope: {},
      controller: [
        '$rootScope',
        'ApplicationService',
        'ImageService',
        'ErrorService',
        CreateAppCtrl
      ],
      controllerAs: 'createApp',
      bindToController: true
    };
  }

  function CreateAppCtrl($rootScope, ApplicationService, ImageService, ErrorService) {

    var vm = this;
    vm.applicationName = '';
    vm.serverImages = [];
    vm.serverImageChoice = {};
    vm.serverImageSelect2 = undefined;
    
    vm.group = [];
    vm.notValidated = true;
    vm.message = '';
    vm.isPending = false;
    vm.createApplication = createApplication;
    vm.isValid = isValid;

    vm.selectConfig = {
      optgroupField: 'idPrefixEnv',
      optgroupLabelField: 'title',
      maxItems: 1,
      valueField: 'id',
      labelField: 'name',
      searchField: 'name',
      placeholder: 'Select Server',
      render: {
        optgroup_header: function(data, escape) {
          return '<div class="label">' + escape(data.title) + '</div>';
        }
      },
      onChange(value) {
        vm.serverImageChoice =  vm.serverImages[vm.serverImages.map(function(x) {return x.id; }).indexOf(+value)];
      }
    };

    init();

    function init() {
      ImageService.findEnabledServer()
        .then(success)
        .catch(error);

      function success(serverImages) {
        vm.serverImages = serverImages;
      
         serverImages.forEach(function (element, index) {
          var rang = vm.group.map(function(x) {return x.title; }).indexOf(serverImages[index].prefixEnv);
          if(rang != -1) {    
            vm.serverImages[index].idPrefixEnv = vm.group[rang].idPrefixEnv;
          } else {
            var prefiEnv = serverImages[index].prefixEnv;
            var idPrefixEnv = vm.group.length + 1;
            vm.serverImages[index].idPrefixEnv = idPrefixEnv;
            vm.group.push({
              id: idPrefixEnv,
              title: prefiEnv,
              idPrefixEnv: idPrefixEnv
            });
          }
         });

        vm.serverImageChoice = serverImages[0];
      }

      function error(response) {
        ErrorService.handle(response);
      }
    }

    function createApplication(applicationName, serverName) {
      // emit app:creating event to display a shadow app during creation process
      vm.isPending = true;
      $rootScope.$broadcast('app:creating', applicationName);

      ApplicationService.create(applicationName, serverName)
        .then(success)
        .catch(error);

      function success() {
        // reset du  formulaire
        vm.createAppForm.$setPristine();
        vm.applicationName = '';
        vm.isPending = false;
      }

      function error(response) {
        vm.message = response.data.message;
        $rootScope.$broadcast('app:create:fail', applicationName);
      }
    }

    function isValid(applicationName, serverName) {
      ApplicationService.isValid(applicationName, serverName)
        .then(success)
        .catch(error);

      function success() {
        vm.notValidated = false;
        vm.message = '';
      }

      function error(response) {
        vm.message = response.data.message;
        vm.notValidated = true;
        vm.isPending = false;
      }
    }
  }
})();
