package com.platon.browser.util.decode;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @description:
 * @author: chendongming@juzix.net
 * @create: 2019-11-21 16:18:55
 **/
public class TxInputUtilTest {

    @Test
    public void test(){
        // REPORT
        TxInputUtil.decode("0xf9065e83820bb801b90656b906537b227072657061726541223a7b2265706f6368223a302c22766965774e756d626572223a302c22626c6f636b48617368223a22307861656238656262643035386466326364313962633235613737396534643862383337343338336236303530613464386163626238306232353461343839343030222c22626c6f636b4e756d626572223a31383939302c22626c6f636b496e646578223a302c22626c6f636b44617461223a22307832643764313433663531343262383665376535306137356465333234626163393438323333663862326537663063383966316139353430656333613533636661222c2276616c69646174654e6f6465223a7b22696e646578223a302c2261646472657373223a22307863666535316438356639393635663664303331653465336363653638386561623763393565393430222c226e6f64654964223a226266633964363537386261623465353130373535353735653437623764313337666366306164306263663130656434643032333634306466623431623139376239663064383031346534376563626534643531663135646235313430303963626461313039656263663062376166653036363030643664343233626237666266222c22626c735075624b6579223a22623437313337393764323936633966653137343964323265623539623033643936393461623839366237313434396230653664616632643165636233613964336436653963323538623337616362326430376661383262636235356365643134346662346230353664366364313932613530393835393631356230393031323864366535363836653834646634373935316531373831363235363237393037303534393735663736653432376461386433326433663330623961353365363066227d2c227369676e6174757265223a2230783365626161633566643634636236363266623030353634656562326335376130666662303336383539663134326133653837653663373333663533656537373335316139353462363266313035643430623435306635633763346431323538653030303030303030303030303030303030303030303030303030303030303030227d2c227072657061726542223a7b2265706f6368223a302c22766965774e756d626572223a302c22626c6f636b48617368223a22307861616361323563663862386634373737373535626164333235363337653433386361393234633062316463323931623164666561363431376266303632323730222c22626c6f636b4e756d626572223a31383939302c22626c6f636b496e646578223a302c22626c6f636b44617461223a22307833646432353035343736643234386466633461656266613139653561623733373065636130383863616437323936393164386138373431313531386536363037222c2276616c69646174654e6f6465223a7b22696e646578223a302c2261646472657373223a22307863666535316438356639393635663664303331653465336363653638386561623763393565393430222c226e6f64654964223a226266633964363537386261623465353130373535353735653437623764313337666366306164306263663130656434643032333634306466623431623139376239663064383031346534376563626534643531663135646235313430303963626461313039656263663062376166653036363030643664343233626237666266222c22626c735075624b6579223a22623437313337393764323936633966653137343964323265623539623033643936393461623839366237313434396230653664616632643165636233613964336436653963323538623337616362326430376661383262636235356365643134346662346230353664366364313932613530393835393631356230393031323864366535363836653834646634373935316531373831363235363237393037303534393735663736653432376461386433326433663330623961353365363066227d2c227369676e6174757265223a2230786261316335363536386439376437323430393736303133383135663363333235613137356461373761393136323463333462386532646266616464643161636165313239386461316534303363396363336638303735346533376633336630653030303030303030303030303030303030303030303030303030303030303030227d7d");
        // STAKE_EXIT
        TxInputUtil.decode("0xf848838203ebb842b840bfc9d6578bab4e510755575e47b7d137fcf0ad0bcf10ed4d023640dfb41b197b9f0d8014e47ecbe4d51f15db514009cbda109ebcf0b7afe06600d6d423bb7fbf");
        // STAKE_MODIFY
        TxInputUtil.decode("0xf893838203e9959460ceca9c1290ee56b98d4e160ef0453f7c40d219b842b8400aa9805681d8f77c05f317efc141c97d5adb511ffb51f5a251d2d7a4a3a96d9a12adf39f06b702f0ccdff9eddc1790eb272dca31b0c47751d49b5931c58701e7919035464436384236393030313036333242888763646d2d3030348c8b5757572e4343432e434f4d8c8b4e6f6465206f662043444d");
        // PROPOSAL_VOTE
        TxInputUtil.decode("0xf8b4838207d3b842b840459d199acb83bfe08c26d5c484cbe36755b53b7ae2ea5f7a5f0a8f4c08e843b51c4661f3faa57b03b710b48a9e17118c2659c5307af0cc5329726c13119a6b85a1a0f9fb1d9f64ec2573cad0cec662ec1ffcf7207d8c2ebac56d13b5e1ef0a2555f30183820703b843b841c943f49857fa2912c4bafd5217bf9e1ccb646a821cb516da014d4b38913a69dd786343926ce377686914e01760bd546478f63828559ef1124dbf297aff2124e401");
        // RESTRICTING_CREATE
        TxInputUtil.decode("0xf683820fa0959460ceca9c1290ee56b98d4e160ef0453f7c40d2199bdacc8203e8884563918244f40000cc8207d0880853a0d2313c0000");
        // PROPOSAL_VOTE
        TxInputUtil.decode("0xf8b4838207d3b842b840459d199acb83bfe08c26d5c484cbe36755b53b7ae2ea5f7a5f0a8f4c08e843b51c4661f3faa57b03b710b48a9e17118c2659c5307af0cc5329726c13119a6b85a1a009ffb5916c2f40f86ab3d395957fb6b0d5881be5e61fe20c408b4300a811f2320183820703b843b841c943f49857fa2912c4bafd5217bf9e1ccb646a821cb516da014d4b38913a69dd786343926ce377686914e01760bd546478f63828559ef1124dbf297aff2124e401");
        // STAKE_CREATE
        TxInputUtil.decode("0xf901a1838203e88180959460ceca9c1290ee56b98d4e160ef0453f7c40d219b842b840bfc9d6578bab4e510755575e47b7d137fcf0ad0bcf10ed4d023640dfb41b197b9f0d8014e47ecbe4d51f15db514009cbda109ebcf0b7afe06600d6d423bb7fbf9190354644363842363930303130363332428a897a726a2d6e6f6465318e8d7777772e62616964752e636f6d96956368656e6461692d6e6f6465312d64657461696c738c8b108b2a2c2802909400000083820703b843b841db3b6fc83e683dd3ce915e691f6095ebfef951c0828250ca2c7a5eebc6eed92a6531123b17f8d987460890a470a009aab522ac7ceb311f2367dae9aec6466baf00b862b860b4713797d296c9fe1749d22eb59b03d9694ab896b71449b0e6daf2d1ecb3a9d3d6e9c258b37acb2d07fa82bcb55ced144fb4b056d6cd192a509859615b090128d6e5686e84df47951e1781625627907054975f76e427da8d32d3f30b9a53e60fb842b8409b40eb38ee734640d38e433fde9c075ae123d6238e450c0b8437ef3ece9e4e6378c0f54db1105ee0045baecf4a9cad4e8c1d4d5e8c91c42c4bbd61fd87ec4d52");
        // STAKE_INCREASE
        TxInputUtil.decode("0xf857838203eab842b8400aa9805681d8f77c05f317efc141c97d5adb511ffb51f5a251d2d7a4a3a96d9a12adf39f06b702f0ccdff9eddc1790eb272dca31b0c47751d49b5931c58701e781808c8b0422ca8b0a00a425000000");
        // DELEGATE_CREATE
        TxInputUtil.decode("0xf854838203ec8180b842b84077fffc999d9f9403b65009f1eb27bae65774e2d8ea36f7b20a89f82642a5067557430e6edfe5320bb81c3666a19cf4a5172d6533117d7ebcd0f2c82055499050898898a7d9b8314c0000");
        // DELEGATE_EXIT
        TxInputUtil.decode("0xf858838203ed83820d70b842b84077fffc999d9f9403b65009f1eb27bae65774e2d8ea36f7b20a89f82642a5067557430e6edfe5320bb81c3666a19cf4a5172d6533117d7ebcd0f2c820554990508b8a69e10de76676d0800000");
        // PROPOSAL_CANCEL
        TxInputUtil.decode("0xf86f838207d5b842b8400aa9805681d8f77c05f317efc141c97d5adb511ffb51f5a251d2d7a4a3a96d9a12adf39f06b702f0ccdff9eddc1790eb272dca31b0c47751d49b5931c58701e78382313101a1a0d62f8b78e95841edddd69970f5f8a6fefd837923af6ed41f8526991eb5f56297");
        // PROPOSAL_TEXT
        TxInputUtil.decode("0xf84c838207d0b842b840ff40ac420279ddbe58e1bf1cfe19f4b5978f86e7c483223be26e80ac9790e855cb5d7bd743d94b9bd72be79f01ee068bc1fefe79c06ba9cd49fa96f52c7bdce083827334");
        // PROPOSAL_UPGRADE
        TxInputUtil.decode("0xf84f838207d1b842b8404cc7be9ec01466fc4f14365f6700da36f3eb157473047f32bded7b1c0c00955979a07a8914895f7ee59af9cb1e6b638aa57c91a918f7a84633a92074f286b20838848301000014");
        // VERSION_DECLARE
        TxInputUtil.decode("0xf891838207d4b842b8400aa9805681d8f77c05f317efc141c97d5adb511ffb51f5a251d2d7a4a3a96d9a12adf39f06b702f0ccdff9eddc1790eb272dca31b0c47751d49b5931c58701e783820703b843b8414a04fd81a170cf4e3c5c6876b73907fe51eb82275dd3f112ce1487ecbab9e43a285e00301e6ece412edcd3efaec0566486afc3315e70ad84ccb2001a32c3c36d00");
        // PROPOSAL_PARAMETER
        TxInputUtil.decode("0xf88f838207d2b842b8404cc7be9ec01466fc4f14365f6700da36f3eb157473047f32bded7b1c0c00955979a07a8914895f7ee59af9cb1e6b638aa57c91a918f7a84633a92074f286b2089291313537353336363034352e36303934393988877374616b696e678f8e7374616b655468726573686f6c649a9931353030303030303030303030303030303030303030303030");
        assertTrue(true);
    }

}
