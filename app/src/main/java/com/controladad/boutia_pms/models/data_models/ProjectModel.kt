package com.controladad.boutia_pms.models.data_models

object ProjectModel {

    //مسئولیت در گروه
    data class ResponsibilityInGroup(var responsibility: String?)

    // تاریخ بازدید
    data class CheckupDate(var date: String?)

    //نوع بازدید
    data class CheckupType(var type: String?)

    //شماره دیسپاچینگ
    data class BarCode(var number: String?, var type: String?)

    //نام خط
    data class MissionName(var name: String?)

    //تعداد مدار
    data class CircuitCount(var count: String?)

    //ولتاژ
    data class Voltage(var voltage: String?)

    //شماره دکل
    data class ElectricTowerNumber(var number: String?)


    //تیپ دکل
    data class ElectricTowerType(var type: String?, var cementElectricTowerType: CementElectricTowerType?)

    //نوع تیر
    data class CementElectricTowerType(var cementElectricTowerType: String?)

    //نوع دکل

    class NoeDakal(var type: String?)

    //در معرض سیل
    data class FloodCondition(var condition: String?, var badCondition: FloodBadCondition?)

    data class FloodBadCondition(var damageAmount: String?,
                                 var dangerAmount: String?)


    //فونداسیون
    data class Foundation(var condition: String?, var foundationIfBad: FoundationIfBad?)

    data class FoundationIfBad(var khakBardari: String?,
                               var khakRizi: String?,
                               var concreteDamage: String?,
                               var horizontalCrack: String?,
                               var ConcreteEat: String?,
                               var verticalCrack: String?,
                               var floodNet: String?,
                               var others: String?,
                               var grassNeedsToBeCleaned: String?,
                               var ifGrassNeedsToBeCleaned: IfGrassNeedsToBeCleaned?,
                               var tedadeMahar: String?,
                               var description: String?)

    data class IfGrassNeedsToBeCleaned(var grassCount: String?)

    //سیم زمین
    data class EarthWire(var healthCondition: String?, var earthWireBadConditions: EarthWireBadConditions?)

    data class EarthWireBadConditions(var serghtat: String?,
                                      var poosidegi: String?,
                                      var biroonBoodaneSimeZamin: String?,
                                      var kolomp: String?,
                                      var paregi: String?,
                                      var others: String?)

    //تابلو
    data class Panel(var condition: String?, var panelBadConditions: PanelBadConditions?)

    data class PanelBadConditions(var panelHavayee: PanelHavayee?,
                                  var panelNumber: PanelNumber?,
                                  var description: String?,
                                  var phasePanel: PhasePanel?,
                                  var panelDomain: PanelDomain?,
                                  var panelHavayeeCondition: String?,
                                  var panelNumberCondition: String?,
                                  var phasePanelCondition: String?,
                                  var panelDomainCondition: String?)

    data class PanelHavayee(var condition: String?)
    data class PanelNumber(var condition: String?)
    data class PhasePanel(var condition: String?)
    data class PanelDomain(var condition: String?)

    //پیچ و مهره
    data class BoltsAndNuts(var condition: String?, var boltsAndNutsConditionsIfBad: BoltsAndNutsConditionsIfBad?)

    data class BoltsAndNutsConditionsIfBad(var pichMohreItemList: ArrayList<PichMohreItem>?, var description: String?)

    data class PichMohreItem(var description: String?, var size: String?, var amount: String?)

    //پیچ پله
    data class StairBolts(var condition: String?, var stairBoltsBadConditions: StairBoltsBadConditions?)

    data class StairBoltsBadConditions(var description: String?,
                                       var amount: String?,
                                       var rustiness: String?,
                                       var niazeBeTamir: String?)

    //خار ضد صعود
    data class Thorn(var condition: String?,
                     var thornBadConditions: ThornBadConditions?)

    data class ThornBadConditions(var amount: String?,
                                  var rustiness: String?,
                                  var description: String?)

    //پلیت
    data class Plate(var condition: String?,
                     var plateIfBadConditions: PlateIfBadConditions?)

    data class PlateIfBadConditions(var description: String?,
                                    var plateIfBadCondition: List<PlateIfBadCondition>?)

    data class PlateIfBadCondition(var platePlace: String?,
                                   var numberOfMissingPlates: String?,
                                   var plateThornAndNutsShortage: String?,
                                   var plateNumber: String?,
                                   var isPlateRusty: String?,
                                   var isPlateBroken: String?)

    //نبشی
    data class Corner(var condition: String?,
                      var cornerConditionIfBad: CornerConditionIfBad?)

    data class CornerConditionIfBad(var rustySizeInMeter: String?,
                                    var cornerCurve: String?,
                                    var oneWayOpen: String?,
                                    var description: String?,
                                    var mogheiat: String?,
                                    var shomareNabshi: String?,
                                    var ifCornerStolen: List<CornerStolen>?)

    data class CornerStolen(var amount: String?, var size: String?, var width: String?)

    //هادی های فاز و ملحقات
    data class HadiHayePhaseVaMolhaghat(var dispatching: String?,
                                        var condition: String?,
                                        var badCondition: HadiHayePhaseVaMolhaghatBadCondition?)

    data class HadiHayePhaseVaMolhaghatBadCondition(var hadiBadCondition: HadiBadCondition,
                                                    var phaseBadCondition: PhaseBadCondition,
                                                    var description: String?)

    data class HadiBadCondition(var jamper: String?,
                                var loolePressMiani: String?,
                                var spiser: String?,
                                var loolePressEntehaee: String?,
                                var reeper: String?,
                                var bandaj: String?,
                                var goyeEkhtar: String?)

    data class PhaseBadCondition(var zangZadegi: String?,
                                 var badKardegiSim: String?,
                                 var damper: String?,
                                 var armorad: String?,
                                 var colomp: String?,
                                 var shotorGalooyee: String?,
                                 var vazneh: String?)

    //زنجیره و ملحقات
    data class IsolationChains(var dispatching: String?,
                               var aType: String?,
                               var bType: String?,
                               var cType: String?,
                               var aCondition: String?,
                               var bCondition: String?,
                               var cCondition: String?,
                               var ifAConditionBad: IsolationChainsBadCondition?,
                               var ifBConditionBad: IsolationChainsBadCondition?,
                               var ifCConditionBad: IsolationChainsBadCondition?)

    data class IsolationChainsBadCondition(var damageType: String?, var numberOfDamage: String?,
            //پارگی
                                           var isTeared: String?,
            //کج بودن زنجیره
                                           var isTilted: String?,
                                           var offset: String?,
            //لب پریدگی
                                           var nick: String?,
            //کپ و پین مقره
                                           var capAndPins: String?,
            //درحال بیرون آمدن پین مقره
                                           var insolatorPinDropping: String?,
            //نداشتن اشپیل
                                           var dontHaveEshpil: String?,
            //نداشتن پیچ و مهره
                                           var dontHaveBoltsAndNuts: String?,
            //آلودگی
                                           var polution: String?,
                                           var polutionPercent: String?,
            //نوع آسیب مقره
                                           var noeAsibeMaghare: String?,
                                           var tedadeShekastegiYaSookhtegi: String?,
            //توضیحات
                                           var description: String?)


    //یراق
    data class Fittings(var dispatching: String?,
                        var aCondition: String?, var bCondition: String?, var cCondition: String?,
                        var ifAConditionBad: FittingsBadCondition?,
                        var ifBConditionBad: FittingsBadCondition?,
                        var ifCConditionBad: FittingsBadCondition?)

    //برقگیر-کرونارینگ,کفی,اتصالات
    data class FittingsBadCondition(var lightningArresterDescription: String?,
                                    var cronarigDescription: String?,
                                    var insolesDescription: String?,
                                    var connectionDescription: String?,
                                    var description: String?)

    data class SimeMohafezVaMolhaghat(var type: String?,
                                      var takShieldFields: TakShieldFields?,
                                      var doShieldFields: DoShieldFields?)

    data class TakShieldFields(var jenseSim: String?,
                               var opgwFields: OpgwFields?,
                               var condition: String?,
                               var simeMohafezBadCondition: SimeMohafezBadCondition?)

    data class OpgwFields(var jointBox: String?)
    data class SimeMohafezBadCondition(var acharKeshi: String?,
                                       var damper: String?,
                                       var loolePress: String?,
                                       var kafi: String?,
                                       var kolomp: String?,
                                       var armorad: String?,
                                       var jamper: String?,
                                       var eshpil: String?,
                                       var description: String?,
                                       var goyeEkhtar: String?)

    data class DoShieldFields(var jenseSimA: String?,
                              var opgwFieldsA: OpgwFields?,
                              var conditionA: String?,
                              var simeMohafezBadConditionA: SimeMohafezBadCondition?,
                              var jenseSimB: String?,
                              var opgwFieldsB: OpgwFields?,
                              var conditionB: String?,
                              var simeMohafezBadConditionB: SimeMohafezBadCondition?)

    data class LaneParande(var condition: String?,
                           var badCondition: LaneParandeBadCondition?)

    data class LaneParandeBadCondition(var tedadeLaneParandeGard: String?,
                                       var tedadeLaneParandePhase: String?,
                                       var tedadeLaneParandeDakal: String?,
                                       var description: String?)

    data class AshiaEzafe(var condition: String?,
                          var badCondition: AshiaEzafeBadCondition?)

    data class AshiaEzafeBadCondition(var tedadeashiaEzafeGard: String?,
                                      var tedadeashiaEzafePhase: String?,
                                      var tedadeashiaEzafeDakal: String?,
                                      var description: String?)

    data class TaghatoBaJadeAsli(var condition: String?,
                                 var badCondition: TaghatoBaJadeAsliBadCondition?)

    data class TaghatoBaJadeAsliBadCondition(var tedad: String?,
                                             var description: String?)

    data class Mavne(var condition: String?,
                     var badCondition: MavaneBadCondition?)

    data class MavaneBadCondition(var description: String?)
    data class Ensheab(var condition: String?,
                       var badCondition: EnsheabBadCondition?)

    data class EnsheabBadCondition(var description: String?)
    data class TaghatoBa20k(var condition: String?,
                            var badCondition: TaghatoBa20kBadCondition?)

    data class TaghatoBa20kBadCondition(var tedad: String?,
                                        var description: String?)

    data class LoolePressMiani(var condition: String?,
                               var badCondition: LoolePressMianiBadCondition?)

    data class LoolePressMianiBadCondition(var description: String?,
                                           var shomareShield: String?,
                                           var shomareFaze: String?,
                                           var tedad: String?)

    data class VaziateHarim(var condition: String?,
                            var badCondition: VaziateHarimBadCondition?)

    data class VaziateHarimBadCondition(var description: String?,
                                        var sakhtoSaz: String?,
                                        var darakht: String?,
                                        var jade: String?,
                                        var keshavarzi: String?,
                                        var looleHayeSookhtResani: String?,
                                        var others: String?,
                                        var railway: String? = null,
                                        var kanal: String? = null)

    data class VaziateMasir(var condition: String?,
                            var badCondition: VaziateMasirBadCondition?)

    data class VaziateMasirBadCondition(var description: String?,
                                        var amount: String?)

    data class KhamooshKardaneKhat(var description: String?,
                                   var vaziate: String?)

    data class DakalBohrani(var description: String?,
                                   var vaziate: String?)

    data class DakalFieldes(var towerType: ProjectModel.ElectricTowerType?, var noeDakal: ProjectModel.NoeDakal?, var checkupType: CheckupType?, var missionName: String?, var towerName: String?, var towerNumber: ProjectModel.ElectricTowerNumber?,
                            var circuitCount: ProjectModel.CircuitCount?, var barCodeList: List<ProjectModel.BarCode>?, var vaziateTakhir: String?, var elateTakhir: String?, var nextTowerCode: String?)

    data class Goy(
            var condition: String?,
            var badCondition: GoyBadCondition?
    )

    data class GoyBadCondition(var shieldNumber: String?,
                               var fazNumber: String?,
                               var description: String?)

    data class Jooshkari(
            var condition: String?,
            var badCondition: JooshkariBadCondition?
    )

    data class JooshkariBadCondition(
            var tooleJooshkariBeMetr: String?,
            var desc: String?
    )


    class ReviewModel(
            var mid: Int?,
            var responsibilityInGroup: ProjectModel.ResponsibilityInGroup,
            var checkupDate: ProjectModel.CheckupDate,
            var checkupType: CheckupType,
            var barCodeList: List<BarCode>,
            var missionName: MissionName,
            var circuitCount: CircuitCount,
            var voltage: Voltage,
            var electricTowerNumber: ElectricTowerNumber,
            var electricTowerType: ElectricTowerType,
            var floodCondition: FloodCondition,
            var jooshkari: Jooshkari,
            var foundation: Foundation,
            var noeDakal: NoeDakal,
            var towerName: String?,
            var earthWire: EarthWire,
            var panel: Panel,
            var boltsAndNuts: BoltsAndNuts,
            var stairBolts: StairBolts?,
            var thorn: Thorn,
            var plate: Plate,
            var corner: Corner,
            var goy: Goy,
            var hadiHayePhaseVaMolhaghatAList: List<HadiHayePhaseVaMolhaghat>?,
            var hadiHayePhaseVaMolhaghatBList: List<HadiHayePhaseVaMolhaghat>?,
            var hadiHayePhaseVaMolhaghatCList: List<HadiHayePhaseVaMolhaghat>?,
            var fittingsList: List<Fittings>,
            var isolationChainsList: List<IsolationChains>,
            var simeMohafezVaMolhaghat: SimeMohafezVaMolhaghat,
            var laneParande: LaneParande,
            var ashiaEzafe: AshiaEzafe,
            var taghatoBaJadeAsli: TaghatoBaJadeAsli,
            var mavane: Mavne,
            var ensheab: Ensheab,
            var taghatoBa20k: TaghatoBa20k,
            var loolePressMiani: LoolePressMiani?,
            var vaziateHarim: VaziateHarim,
            var vaziateMasir: VaziateMasir,
            var khamooshKardaneKhat: KhamooshKardaneKhat,
            var imagePathList: ArrayList<String>,
            var imageFidList: ArrayList<String>,
            var vaziateTakhir: String?,
            var elateTakhir: String?,
            var nextTowerCode: String?,
            var scanType: String?,
            var dakalBohrani: DakalBohrani
    ) {

        companion object {


            fun create(): ReviewModel = ReviewModel(
                    0
                    , ProjectModel.ResponsibilityInGroup(null),
                    CheckupDate(null),
                    CheckupType(null),
                    ArrayList(),
                    MissionName(null),
                    CircuitCount(null),
                    Voltage(null),
                    ElectricTowerNumber(null),
                    ElectricTowerType(null, null),
                    FloodCondition(null, null),
                    Jooshkari(null,null),
                    Foundation(null, null),
                    NoeDakal(null),
                    null,
                    EarthWire(null, null),
                    Panel(null, null),
                    BoltsAndNuts(null, null),
                    StairBolts(null, null),
                    Thorn(null, null),
                    Plate(null, null),
                    Corner(null, null),
                    Goy(null, null),
                    ArrayList(), ArrayList(), ArrayList(), ArrayList(), ArrayList(),
                    SimeMohafezVaMolhaghat(null, null, null),
                    LaneParande(null, null),
                    AshiaEzafe(null, null),
                    TaghatoBaJadeAsli(null, null),
                    Mavne(null, null),
                    Ensheab(null, null),
                    TaghatoBa20k(null, null),
                    LoolePressMiani(null, null),
                    VaziateHarim(null, null),
                    VaziateMasir(null, null),
                    KhamooshKardaneKhat(null, null),
                    ArrayList(),
                    ArrayList(),
                    null,
                    null,
                    null,
                    null,
                    DakalBohrani(null,null)
            )

            fun helperCreate(): ReviewModel = ReviewModel(
                    0
                    , ProjectModel.ResponsibilityInGroup(null),
                    CheckupDate(null),
                    CheckupType(null),
                    ArrayList(),
                    MissionName(null),
                    CircuitCount(null),
                    Voltage(null),
                    ElectricTowerNumber(null),
                    ElectricTowerType(null, CementElectricTowerType(null)),
                    FloodCondition(null, FloodBadCondition(null, null)),
                    Jooshkari(null,JooshkariBadCondition(null,null)),
                    Foundation(null, FoundationIfBad(null, null, null, null, null, null, null, null,
                            null, IfGrassNeedsToBeCleaned(null), null, null)),
                    NoeDakal(null),
                    null,
                    EarthWire(null, EarthWireBadConditions(null, null, null, null, null, null)),
                    Panel(null, PanelBadConditions(PanelHavayee(null),
                            PanelNumber(null), null, PhasePanel(null),
                            PanelDomain(null), null, null,
                            null, null)),
                    BoltsAndNuts(null, BoltsAndNutsConditionsIfBad(ArrayList(), null)),
                    StairBolts(null, StairBoltsBadConditions(null, null, null, null)),
                    Thorn(null, ThornBadConditions(null, null, null)),
                    Plate(null, PlateIfBadConditions(null, ArrayList())),
                    Corner(null, CornerConditionIfBad(null, null, null, null, null, null, ArrayList())),
                    Goy(null, GoyBadCondition(null,null,null))
                    , ArrayList(), ArrayList(), ArrayList(), ArrayList(), ArrayList(),
                    SimeMohafezVaMolhaghat(null, TakShieldFields(null, OpgwFields(null), null,
                            SimeMohafezBadCondition(null, null, null, null, null, null, null, null, null, null)),
                            DoShieldFields(null, OpgwFields(null), null,
                                    SimeMohafezBadCondition(null, null, null, null, null, null, null, null, null, null),
                                    null, OpgwFields(null), null,
                                    SimeMohafezBadCondition(null, null, null, null, null, null, null, null, null, null))),
                    LaneParande(null, LaneParandeBadCondition(null, null, null, null)),
                    AshiaEzafe(null, AshiaEzafeBadCondition(null, null, null, null)),
                    TaghatoBaJadeAsli(null, TaghatoBaJadeAsliBadCondition(null, null)),
                    Mavne(null, MavaneBadCondition(null)),
                    Ensheab(null, EnsheabBadCondition(null)),
                    TaghatoBa20k(null, TaghatoBa20kBadCondition(null, null)),
                    LoolePressMiani(null, LoolePressMianiBadCondition(null, null, null, null)),
                    VaziateHarim(null, VaziateHarimBadCondition(null, null, null, null, null, null, null)),
                    VaziateMasir(null, VaziateMasirBadCondition(null, null)),
                    KhamooshKardaneKhat(null, null),
                    ArrayList(),
                    ArrayList(),
                    null,
                    null,
                    null,
                    null,
                    DakalBohrani(null,null)
            )
        }
    }
}

