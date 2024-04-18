package com.controladad.boutia_pms.models.retrofit_models

import com.google.gson.annotations.SerializedName

object RetrofitModels {

    // Version CheckModel
    data class VersionCheck(@SerializedName("version") val version: String?,
                            @SerializedName("path") val path: String?,
                            @SerializedName("ostans") val ostans: List<Ostans>?)


    // Version CheckModel
    data class DateTimeModel(@SerializedName("unixtime") val unixTime: Long?,
                            @SerializedName("datetime") val dateTime: String?,
                            @SerializedName("dst") val dst: Boolean?,
                            @SerializedName("raw_offset") val rawOffset: Int?,
                            @SerializedName("dst_offset") val dstOffset: Int?)

    data class Ostans(@SerializedName("name") val name: String?,
                      @SerializedName("url") val url: String?)


    //Login Model

    data class PreUser(@SerializedName("user") val user: User?,
                       @SerializedName("sessid") val sessionId: String?,
                       @SerializedName("session_name") val sessionName: String?,
                       @SerializedName("token") val token: String?)

    data class User(@SerializedName("uid") val userId: String?,
                    @SerializedName("field_usernames") val userName: String?,
                    @SerializedName("roles") val userRole: Map<String, String>?,
                    @SerializedName("field_phone") val phoneNumber: String?,
                    @SerializedName("picture") val photo: Picture?,
                    @SerializedName("field_group") val group: Group?)

    data class PreUserForCheck(@SerializedName("user") val user: UserForCheck?,
                               @SerializedName("sessid") val sessionId: String?,
                               @SerializedName("session_name") val sessionName: String?,
                               @SerializedName("token") val token: String?)

    data class UserForCheck(
            @SerializedName("roles") val userRole: Map<String, String>?)

    data class Group(@SerializedName("name") val groupName: String?)

    data class Picture(@SerializedName("uri") val pictureUri: String?)

    //Mission Model

    data class MissionBazdidX(
            @SerializedName("op_counts") val opCount: String?,
            @SerializedName("operations") val operationList: List<Missions>?
    )

    data class Missions(@SerializedName("nid") val nid: String?,
                        @SerializedName("title") val missionsTitle: String?,
                        @SerializedName("op_group") val missionOperationGroup: OPGroup?,
                        @SerializedName("op_type") val missionType: MissionType?,
                        @SerializedName("op_lines") val missionLine: MutableList<MissionLine>?,
                        @SerializedName("op_start") val missionStartDate: String?,
                        @SerializedName("op_start_stamp") val timeStampStart: String?,
                        @SerializedName("op_end_stamp") val timeStampEnd: String?,
                        @SerializedName("op_end") val missionEndDate: String?)

    data class OPGroup(@SerializedName("tid") val tid: String?,
                       @SerializedName("name") val groupName: String?)

    data class MissionType(@SerializedName("name") val type: String?,
                           @SerializedName("tid") val typeTid: String?)

    data class MissionLine(@SerializedName("nid") val lineNid: String?,
                           @SerializedName("title") val lineName: String?,
                           @SerializedName("voltage") val lineVoltage: String?,
                           @SerializedName("width") val lineWidth: String?,
                           @SerializedName("code") val lineCode: String?)

    data class ImageResponseModel(@SerializedName("fid") val fid: String?)

    data class RepairListX(@SerializedName("op_counts") val repairOperationCount: String?,
                           @SerializedName("operations") val repairList: List<RepairList>?)

    data class RepairList(@SerializedName("details") val repairMissionDetails: RepairMissionDetails?)

    data class RepairMissionDetails(@SerializedName("l2") val lineDescription: LineDescription?,
                                    @SerializedName("date") val lineDate: LineDate?,
                                    @SerializedName("group") val group: OPGroup?,
                                    @SerializedName("type") val type: RepairMissionType?,
                                    @SerializedName("nid") val mid: String?)

    //Repair Mission
    data class LineDescription(@SerializedName("title") val title: String?,
                               @SerializedName("code") val code: String?,
                               @SerializedName("multiple_l2") val multipleL2: List<MultipleL2>)

    data class MultipleL2 (@SerializedName("title") val title:String?,
                           @SerializedName("code") val code : String?)

    data class LineDate(@SerializedName("value") val startDate: String?,
                        @SerializedName("value2") val endDate: String?,
                        @SerializedName("stamp") val stamp: Stamp?)

    data class Stamp(@SerializedName("value") val startDateStamp: String?,
                     @SerializedName("value2") val endDateStamp: String?)

    data class RepairMissionType(@SerializedName("tid") val tid: String?,
                                 @SerializedName("name") val name: String?)

    data class DakalList(@SerializedName("details") val detailsOfDakal: DetailOfDakal?,
                         @SerializedName("dakals") val dakals: List<Dakals>?)

    data class DetailOfDakal(@SerializedName("count_dakals") val dakalsCount: String?)

    data class Dakals(@SerializedName("foundation") val foundations: Foundations?,
                      @SerializedName("operation_nid") val mid: String?,
                      @SerializedName("sim_zamin") val simeZamin: SimZamin?,
                      @SerializedName("tablo") val tablo: Tablo?,
                      @SerializedName("pich") val pich: List<Pich>?,
                      @SerializedName("pich_pelle") val pichePelle: PichPelle?,
                      @SerializedName("khar") val khar: Khar?,
                      @SerializedName("plate") val plate: List<Plate>?,
                      @SerializedName("nabshi") val nabshi: List<Nabshi>?,
                      @SerializedName("seil") val seil: Seil?,
                      @SerializedName("hadi_faz") val hadiFaz: List<HadiFaz>?,
                      @SerializedName("yaragh") val yaragh: List<Yaragh>?,
                      @SerializedName("z_m_m") val zanjireMaghareVaMolhaghat: List<ZMM>?,
                      @SerializedName("ezafe") val ezafe: Ezafe?,
                      @SerializedName("mohafez") val mohafez: List<Mohafez>?,
                      @SerializedName("lane_parande") val laneParande: LaneParande?,
                      @SerializedName("barcode") val barcode: String?,
                      @SerializedName("time") val timeStamp: String?
                      )

    data class Foundations(@SerializedName("field_khak") val khak: ValuesOfRepairMission?,
                           @SerializedName("field_mahar") val mahar: ValuesOfRepairMission?,
                           @SerializedName("field_foundation_sayer") val sayer: ValuesOfRepairMission?,
                           @SerializedName("field_foundation_desc") val desc: ValuesOfRepairMission?,
                           @SerializedName("field_takhrib_beton") val takhribBeton: ValuesOfRepairMission?,
                           @SerializedName("field_tarak_o") val tarakOfoghi: ValuesOfRepairMission?,
                           @SerializedName("field_tarak_a") val tarakAmoodi: ValuesOfRepairMission?,
                           @SerializedName("field_field_khordegi") val khordegi: ValuesOfRepairMission?,
                           @SerializedName("field_toor_seil") val toorSeil: ValuesOfRepairMission?,
                           @SerializedName("field_paksazi_boote") val paksaziBoote: ValuesOfRepairMission?,
                           @SerializedName("field_mizan_boote") val mizaneBoote: ValuesOfRepairMission?)

    data class SimZamin(@SerializedName("field_sim_zamin_bad") val simZaminBad: ValuesOfSimZamin?,
                        @SerializedName("field_sim_zamin_desc") val simZaminDesc: ValuesOfRepairMission?)

    data class Tablo(@SerializedName("field_tablo_shomare_bad") val tabloShomareBad: ValuesOfRepairMission?,
                     @SerializedName("field_tablo_havaei_bad") val tabloHavayeeBad: ValuesOfRepairMission?,
                     @SerializedName("field_tablo_fazi_bad") val tabloFaziBad: ValuesOfRepairMission?,
                     @SerializedName("field_tablo_harim_bad") val tabloHarimBad: ValuesOfRepairMission?,
                     @SerializedName("field_tablo_desc") val tabloDesc: ValuesOfRepairMission?)

    //Tekrar shavande ast
    data class Pich(@SerializedName("field_pich_bad") val pichBad: ValuesOfRepairMission?,
                    @SerializedName("field_size_tedad_12") val pichTedad12: ValuesOfRepairMission?,
                    @SerializedName("field_size_tedad_14") val pichTedad14: ValuesOfRepairMission?,
                    @SerializedName("field_size_tedad_16") val pichTedad16: ValuesOfRepairMission?,
                    @SerializedName("field_size_tedad_sayer") val pichTedadSayer: ValuesOfRepairMission?,
                    @SerializedName("field_pich_desc") val pichDescription: ValuesOfRepairMission?)

    data class PichPelle(@SerializedName("field_pich_pelle_kasri") val pichePelleKasri: ValuesOfRepairMission?,
                         @SerializedName("field_pich_pelle_zang") val pichePelleZang: ValuesOfRepairMission?,
                         @SerializedName("field_pich_pelle_tamir") val pichePelleTamir: ValuesOfRepairMission?,
                         @SerializedName("field_pich_pelle_desc") val pichePelleDisc: ValuesOfRepairMission?)

    data class Khar(@SerializedName("field_khar_kasri") val kharKasri: ValuesOfRepairMission?,
                    @SerializedName("field_khar_zang") val kharZang: ValuesOfRepairMission?,
                    @SerializedName("field_khar_desc") val kharDesc: ValuesOfRepairMission?)

    data class Plate(@SerializedName("field_plate_position") val platePosition: ValuesOfRepairMission?,
                     @SerializedName("field_plate_kasri") val plateKasri: ValuesOfRepairMission?,
                     @SerializedName("field_plate_zang") val plateZang: ValuesOfRepairMission?,
                     @SerializedName("field_plate_kasri_pich") val plateKasriPich: ValuesOfRepairMission?,
                     @SerializedName("field_plate_number") val plateNumber: ValuesOfRepairMission?,
                     @SerializedName("field_plate_shekastegi") val plateShekastegi: ValuesOfRepairMission?,
                     @SerializedName("field_plate_general_desc") val plateDesc: ValuesOfRepairMission?)

    // Tekrar shavande darad
    data class Nabshi(@SerializedName("field_nabshi_position") val nabshiPostion: ValuesOfRepairMission?,
                      @SerializedName("field_nabshi_zang") val nabshiZang: ValuesOfRepairMission?,
                      @SerializedName("field_shomare_nabshi") val nabshiShomareh: ValuesOfRepairMission?,
                      @SerializedName("field_nabshi_enhena") val nabshiEnhena: ValuesOfRepairMission?,
                      @SerializedName("field_nabshi_sarbaz") val nabshiSarbaz: ValuesOfRepairMission?,
                      @SerializedName("field_nabshi_desc") val nabshiDesc: ValuesOfRepairMission?,
                      @SerializedName("field_nabshi_serghat_size") val nabshiSerghatSize: ValuesOfRepairMission?,
                      @SerializedName("field_nabshi_serghat_tedad") val nabshiSerghatTedad: ValuesOfRepairMission?,
                      @SerializedName("field_nabshi_serghat_tool") val nabshiSerghatTool: ValuesOfRepairMission?)

    data class Seil(@SerializedName("field_seil_khesarat") val seilKhesarat: ValuesOfRepairMission?,
                    @SerializedName("field_seil_kharat") val seilKhatar: ValuesOfRepairMission?)

    data class HadiFaz(@SerializedName("field_a_hadi") val hadiA: ValuesOfRepairMission?,
                       @SerializedName("field_b_hadi") val hadiB: ValuesOfRepairMission?,
                       @SerializedName("field_c_hadi") val hadiC: ValuesOfRepairMission?,
                       @SerializedName("field_a_faz") val fazA: ValuesOfRepairMission?,
                       @SerializedName("field_b_faz") val fazB: ValuesOfRepairMission?,
                       @SerializedName("field_c_faz") val fazC: ValuesOfRepairMission?,
                       @SerializedName("field_a_faz_desc") val fazADesc: ValuesOfRepairMission?,
                       @SerializedName("field_b_faz_desc") val fazBDesc: ValuesOfRepairMission?,
                       @SerializedName("field_c_faz_desc") val fazCDesc: ValuesOfRepairMission?)

    data class Yaragh(@SerializedName("field_a_yaragh_iradat") val yaraghIradatA: ValuesOfRepairMission?,
                      @SerializedName("field_b_yaragh_iradat") val yaraghIradatB: ValuesOfRepairMission?,
                      @SerializedName("field_c_yaragh_iradat") val yaraghIradatC: ValuesOfRepairMission?,
                      @SerializedName("field_a_yaragh_desc") val yaraghDescA: ValuesOfRepairMission?,
                      @SerializedName("field_b_yaragh_desc") val yaraghDescB: ValuesOfRepairMission?,
                      @SerializedName("field_c_yaragh_desc") val yaraghDescC: ValuesOfRepairMission?)

    data class ZMM(@SerializedName("field_zmm_a_type") val zmmTypeA: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_b_type") val zmmTypeB: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_c_type") val zmmTypeC: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_a_iradat") val zmmAIradat: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_b_iradat") val zmmBIradat: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_c_iradat") val zmmCIradat: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_a_asib") val zmmAAsib: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_b_asib") val zmmBAsib: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_c_asib") val zmmCAsib: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_a_sookhtegi") val zmmASookhtegi: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_b_sookhtegi") val zmmBSookhtegi: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_c_sookhtegi") val zmmCSookhtegi: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_a_shekastegi") val zmmAShekastegi: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_b_shekastegi") val zmmBShekastegi: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_c_shekastegi") val zmmCSheakstegi: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_a_pin") val zmmAPin: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_b_pin") val zmmBPin: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_c_pin") val zmmCPin: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_a_aloodegi") val zmmAAloodegi: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_b_aloodegi") val zmmBAloodegi: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_c_aloodegi") val zmmCAloodegi: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_a_desc") val zmmADesc: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_b_desc") val zmmBDesc: ValuesOfRepairMission?,
                   @SerializedName("field_zmm_c_desc") val zmmCDesc: ValuesOfRepairMission?)

    data class Ezafe(@SerializedName("field_ezafe_faz") val ezafeFaz: ValuesOfRepairMission?,
                     @SerializedName("field_ezafe_gard") val ezafeGard: ValuesOfRepairMission?,
                     @SerializedName("field_ezafe_daka") val ezafeDakal: ValuesOfRepairMission?,
                     @SerializedName("field_ezafe_desc") val ezafeDesc: ValuesOfRepairMission?)

    data class Mohafez(@SerializedName("field_mohafez_type") val mohafezType: ValuesOfRepairMission?,
                       @SerializedName("field_shield_type") val mohafezShieldType: ValuesOfRepairMission?,
                       @SerializedName("field_jointbox") val mohafezJointBox: ValuesOfRepairMission?,
                       @SerializedName("field_shield_iradat") val mohafezIradat: ValuesOfSimZamin?,
                       @SerializedName("field_mohafez_desc") val mohafezDesc: ValuesOfRepairMission?,
                       @SerializedName("field_shield_good") val mohafezVaziateShield: ValuesOfRepairMission?)

    data class LaneParande(@SerializedName("field_lane_faz") val laneFaz: ValuesOfRepairMission?,
                           @SerializedName("field_lane_dakal") val laneDakal: ValuesOfRepairMission?,
                           @SerializedName("field_lane_gard") val laneGard: ValuesOfRepairMission?,
                           @SerializedName("field_lane_desc") val laneDesc: ValuesOfRepairMission?)


    data class ValuesOfRepairMission(@SerializedName("data") val data: String?,
                                     @SerializedName("label") val label: String?)

    data class ValuesOfSimZamin(@SerializedName("data") val data: List<ValuesOfDataArray>?,
                                @SerializedName("label") val label: String?)

    data class ValuesOfDataArray(@SerializedName("value") val value: String?)
}
