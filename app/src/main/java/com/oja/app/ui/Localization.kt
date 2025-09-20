
package com.oja.app.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.oja.app.data.DeliveryMethod
import java.util.Locale

enum class Language(val nativeName: String) {
    ENGLISH("English"),
    PIDGIN("Pidgin"),
    YORUBA("Yorùbá"),
    HAUSA("Hausa"),
    IGBO("Igbo");

    val strings: AppStrings
        get() = when (this) {
            ENGLISH -> englishStrings
            PIDGIN -> pidginStrings
            YORUBA -> yorubaStrings
            HAUSA -> hausaStrings
            IGBO -> igboStrings
        }
}

data class AppStrings(
    val languageLabel: String,
    val themeLabel: String,
    val welcome: WelcomeStrings,
    val navigation: NavigationStrings,
    val home: HomeStrings,
    val cart: CartStrings,
    val jobs: JobsStrings,
    val tracking: TrackingStrings,
    val transporter: TransporterStrings,
    val vendor: VendorStrings,
    val payments: PaymentsStrings
)

data class WelcomeStrings(
    val title: String,
    val subtitle: String,
    val enterMarket: String,
    val heroContentDescription: String
)

data class NavigationStrings(
    val cart: String,
    val jobs: String,
    val transporter: String
)

data class HomeStrings(
    val addToCart: String
)

data class CartStrings(
    val storeLabel: String,
    val subtotalLabel: String,
    val extraFeeLabel: String,
    val checkout: String,
    val dialogTitle: String,
    val accept: String,
    val cancel: String,
    private val methodLabels: Map<DeliveryMethod, String>,
    val extraFeeMessage: (String) -> String
) {
    fun methodLabel(method: DeliveryMethod): String {
        return methodLabels[method] ?: method.name.lowercase(Locale.ROOT).replaceFirstChar { it.titlecase(Locale.ROOT) }
    }
}

data class JobsStrings(
    val title: String,
    val jobLabel: String,
    val orderLabel: String,
    val unclaimed: String,
    val claimedBy: (String) -> String,
    val track: String,
    val claim: String
)

data class TrackingStrings(
    val noTracking: (String) -> String,
    val back: String,
    val pickup: String,
    val dropoff: String,
    val courier: String,
    val placeholder: String,
    val progress: (Int) -> String
)

data class TransporterStrings(
    val title: String,
    val submit: String
)

data class VendorStrings(
    val title: String,
    val submit: String
)

data class PaymentsStrings(
    val title: String,
    val stagingHint: String,
    val paystackButton: String,
    val flutterwaveButton: String,
    val requireActivity: (String) -> String,
    val sdkMissing: (String) -> String,
    val initError: (String) -> String,
    val verificationFailed: (String) -> String,
    val missingReference: (String) -> String,
    val unexpectedResult: (String, Int) -> String,
    val success: (String, String) -> String,
    val cancelled: (String) -> String,
    val nqrTitle: String,
    val nqrButton: String,
    val qrHint: String,
    val ussdTitle: String,
    val ussdButton: String,
    val ussdHint: String,
    val consentLabel: String,
    val consentDetails: String,
    val referenceLabel: String,
    val ussdCodeLabel: String,
    val bankLabel: String,
    val expiresLabel: String,
    val nqrVerifyButton: String,
    val ussdVerifyButton: String
)

val LocalLanguage = staticCompositionLocalOf { Language.ENGLISH }
val LocalStrings = staticCompositionLocalOf { Language.ENGLISH.strings }

private val englishStrings = AppStrings(
    languageLabel = "Language",
    themeLabel = "Theme",
    welcome = WelcomeStrings(
        title = "Welcome to OJA",
        subtitle = "Your neighborhood market—street to doorstep",
        enterMarket = "Enter Market",
        heroContentDescription = "Field agents illustration"
    ),
    navigation = NavigationStrings(
        cart = "Cart",
        jobs = "Jobs",
        transporter = "Be a Transporter"
    ),
    home = HomeStrings(addToCart = "Add to Cart"),
    cart = CartStrings(
        storeLabel = "Store",
        subtotalLabel = "Subtotal",
        extraFeeLabel = "Extra store fee",
        checkout = "Checkout",
        dialogTitle = "Extra logistics cost",
        accept = "Accept",
        cancel = "Cancel",
        methodLabels = mapOf(
            DeliveryMethod.BICYCLE to "Bicycle",
            DeliveryMethod.BIKE to "Bike",
            DeliveryMethod.CAR to "Car"
        ),
        extraFeeMessage = { store -> "Adding items from $store adds courier cost. Accept?" }
    ),
    jobs = JobsStrings(
        title = "Unclaimed Deliveries",
        jobLabel = "Job",
        orderLabel = "Order",
        unclaimed = "Unclaimed",
        claimedBy = { courier -> "Claimed by $courier" },
        track = "Track",
        claim = "Claim"
    ),
    tracking = TrackingStrings(
        noTracking = { orderId -> "No tracking for $orderId" },
        back = "Back",
        pickup = "Pickup",
        dropoff = "Drop-off",
        courier = "Courier",
        placeholder = "Map placeholder (add Maps API key)",
        progress = { percent -> "Progress: ${percent}%" }
    ),
    transporter = TransporterStrings(
        title = "Transporter Registration",
        submit = "Submit"
    ),
    vendor = VendorStrings(
        title = "Vendor Onboarding",
        submit = "Submit"
    ),
    payments = PaymentsStrings(
        title = "Payments (Test Mode)",
        stagingHint = "Trigger Paystack or Flutterwave transactions against the staging server.",
        paystackButton = "Pay with Paystack",
        flutterwaveButton = "Pay with Flutterwave",
        requireActivity = { gateway -> "$gateway payments require an Activity context" },
        sdkMissing = { gateway -> "$gateway UI SDK is missing from the classpath" },
        initError = { gateway -> "Unable to start $gateway checkout" },
        verificationFailed = { gateway -> "$gateway verification failed" },
        missingReference = { gateway -> "Missing $gateway transaction reference" },
        unexpectedResult = { gateway, code -> "$gateway returned unexpected result $code" },
        success = { gateway, reference -> "$gateway payment successful (ref: $reference)" },
        cancelled = { gateway -> "$gateway payment cancelled" },
        nqrTitle = "NQR (QR Transfer)",
        nqrButton = "Generate NQR Code",
        qrHint = "Ask the buyer to scan the code with their banking app.",
        ussdTitle = "USSD Fallback",
        ussdButton = "Request USSD Code",
        ussdHint = "Dial the code in your bank app then confirm.",
        consentLabel = "I accept the NDPA 2023 data processing notice.",
        consentDetails = "We log payment authorisations per NDPC GAID 2025.",
        referenceLabel = "Reference",
        ussdCodeLabel = "USSD Code",
        bankLabel = "Bank",
        expiresLabel = "Expires",
        nqrVerifyButton = "Verify NQR Payment",
        ussdVerifyButton = "Verify USSD Payment"
    )
)

private val pidginStrings = AppStrings(
    languageLabel = "Language",
    themeLabel = "Theme",
    welcome = WelcomeStrings(
        title = "Welcome to OJA",
        subtitle = "Your area market—straight reach your door",
        enterMarket = "Enter Market",
        heroContentDescription = "Field agents picture"
    ),
    navigation = NavigationStrings(
        cart = "Cart",
        jobs = "Jobs",
        transporter = "Become Transporter"
    ),
    home = HomeStrings(addToCart = "Add am enter Cart"),
    cart = CartStrings(
        storeLabel = "Store",
        subtotalLabel = "Subtotal",
        extraFeeLabel = "Extra store fee",
        checkout = "Checkout",
        dialogTitle = "Extra transport money",
        accept = "Gree",
        cancel = "No do",
        methodLabels = mapOf(
            DeliveryMethod.BICYCLE to "Bicycle",
            DeliveryMethod.BIKE to "Bike",
            DeliveryMethod.CAR to "Motor"
        ),
        extraFeeMessage = { store -> "Add items from $store go add rider money. You gree?" }
    ),
    jobs = JobsStrings(
        title = "Deliveries wey nobody don claim",
        jobLabel = "Job",
        orderLabel = "Order",
        unclaimed = "Never claim",
        claimedBy = { courier -> "Claimed by $courier" },
        track = "Track",
        claim = "Claim"
    ),
    tracking = TrackingStrings(
        noTracking = { orderId -> "We no fit track $orderId" },
        back = "Back",
        pickup = "Pickup",
        dropoff = "Drop-off",
        courier = "Rider",
        placeholder = "Map go show once you set Maps API key",
        progress = { percent -> "Progress: ${percent}%" }
    ),
    transporter = TransporterStrings(
        title = "Transporter Signup",
        submit = "Submit"
    ),
    vendor = VendorStrings(
        title = "Vendor Signup",
        submit = "Submit"
    ),
    payments = PaymentsStrings(
        title = "Payments (Test Mode)",
        stagingHint = "Use staging server test Paystack or Flutterwave pay.",
        paystackButton = "Pay with Paystack",
        flutterwaveButton = "Pay with Flutterwave",
        requireActivity = { gateway -> "$gateway payments need Activity context" },
        sdkMissing = { gateway -> "$gateway UI SDK no dey app" },
        initError = { gateway -> "No fit start $gateway checkout" },
        verificationFailed = { gateway -> "$gateway verification fail" },
        missingReference = { gateway -> "$gateway transaction reference no dey" },
        unexpectedResult = { gateway, code -> "$gateway return unexpected code $code" },
        success = { gateway, reference -> "$gateway payment work (ref: $reference)" },
        cancelled = { gateway -> "$gateway payment cancel" },
        nqrTitle = "NQR (QR Transfer)",
        nqrButton = "Show NQR code",
        qrHint = "Make buyer scan am with dia bank app.",
        ussdTitle = "USSD backup",
        ussdButton = "Request USSD code",
        ussdHint = "Dial d code for your bank phone app, then confirm.",
        consentLabel = "I gree make una process my data per NDPA.",
        consentDetails = "We go log payment authorise as NDPC GAID talk.",
        referenceLabel = "Reference",
        ussdCodeLabel = "USSD Code",
        bankLabel = "Bank",
        expiresLabel = "Time wey e go expire",
        nqrVerifyButton = "Check NQR payment",
        ussdVerifyButton = "Check USSD payment"
    )
)

private val yorubaStrings = AppStrings(
    languageLabel = "Èdè",
    themeLabel = "Àwò",
    welcome = WelcomeStrings(
        title = "Ẹ káàbọ̀ sí OJA",
        subtitle = "Ọjà agbègbè rẹ—láti ọ̀nà dé ẹnu-ọ̀nà",
        enterMarket = "Wọ ọjà",
        heroContentDescription = "Àwòrán aṣojú pápá"
    ),
    navigation = NavigationStrings(
        cart = "Apo rira",
        jobs = "Iṣẹ́",
        transporter = "Di olùrìnà"
    ),
    home = HomeStrings(addToCart = "Fi sí àpo rira"),
    cart = CartStrings(
        storeLabel = "Ọjà",
        subtotalLabel = "Apapọ̀ díẹ̀",
        extraFeeLabel = "Owó ọjà míì",
        checkout = "Sanwó",
        dialogTitle = "Owó ọkọ̀ míì",
        accept = "Gba",
        cancel = "Kọ",
        methodLabels = mapOf(
            DeliveryMethod.BICYCLE to "Kẹ̀kẹ́",
            DeliveryMethod.BIKE to "Okada",
            DeliveryMethod.CAR to "Ọkọ̀ ayọ́kẹ́lẹ́"
        ),
        extraFeeMessage = { store -> "Ìfikún láti $store máa pọ́n owó ọkọ̀. Ṣé o gba?" }
    ),
    jobs = JobsStrings(
        title = "Ẹrù tí kò tíì ní olùrìnà",
        jobLabel = "Iṣẹ́",
        orderLabel = "Àṣẹ",
        unclaimed = "Kò tíì gba",
        claimedBy = { courier -> "Ti $courier" },
        track = "Tọ̀pa",
        claim = "Gba"
    ),
    tracking = TrackingStrings(
        noTracking = { orderId -> "A kò rí tọ́pa fún $orderId" },
        back = "Padà",
        pickup = "Gbigba",
        dropoff = "Fifúnni",
        courier = "Olùrìnà",
        placeholder = "Àwo maapu (fi bọtìnì Maps sílẹ̀)",
        progress = { percent -> "Ìlọsíwájú: ${percent}%" }
    ),
    transporter = TransporterStrings(
        title = "Ìforúkọsílẹ̀ olùrìnà",
        submit = "Firanṣẹ́"
    ),
    vendor = VendorStrings(
        title = "Ìforúkọsílẹ̀ oníṣòwò",
        submit = "Firanṣẹ́"
    ),
    payments = PaymentsStrings(
        title = "Ìsanwó (Àdánwò)",
        stagingHint = "Lo olupin ìdánwò láti ṣàdánwò Paystack tàbí Flutterwave.",
        paystackButton = "Sanwó pẹ̀lú Paystack",
        flutterwaveButton = "Sanwó pẹ̀lú Flutterwave",
        requireActivity = { gateway -> "Ìsanwó $gateway nílò àkóónú Activity" },
        sdkMissing = { gateway -> "SDK UI $gateway kò sí nínú àpamọ́" },
        initError = { gateway -> "A kò lè bẹ̀rẹ̀ ìsanwó $gateway" },
        verificationFailed = { gateway -> "Ìmúdájú $gateway kò ṣáṣeyọrí" },
        missingReference = { gateway -> "A kò rí àmìtọ́kasí $gateway" },
        unexpectedResult = { gateway, code -> "$gateway dá padà kóòdù àìlòye $code" },
        success = { gateway, reference -> "$gateway sanwó ṣeyọrí (àmì: $reference)" },
        cancelled = { gateway -> "Ìsanwó $gateway ti fagilé" },
        nqrTitle = "NQR (Koodu QR)",
        nqrButton = "Ṣẹda koodu NQR",
        qrHint = "Jẹ́ kí oníbàárà ṣàfọwọ̀kọ pẹlu app ilé-ìfowópamọ́ wọn.",
        ussdTitle = "USSD ìpèsè",
        ussdButton = "Béèrè kóòdù USSD",
        ussdHint = "Tẹ kóòdù náà sí orí foonu ilé-ìfowópamọ́ rẹ, kí o jẹ́rìí.",
        consentLabel = "Mo fara mọ́ ìmúlò NDPA 2023.",
        consentDetails = "A ó kọ́ ìforúkọsilẹ̀ gẹ́gẹ́ bí NDPC GAID 2025.",
        referenceLabel = "Ìtọ́kasí",
        ussdCodeLabel = "Kóòdù USSD",
        bankLabel = "Ilé ìfowópamọ́",
        expiresLabel = "Parí ní",
        nqrVerifyButton = "Ṣàyẹ̀wò owó NQR",
        ussdVerifyButton = "Ṣàyẹ̀wò owó USSD"
    )
)

private val hausaStrings = AppStrings(
    languageLabel = "Harshe",
    themeLabel = "Launi",
    welcome = WelcomeStrings(
        title = "Barka da zuwa OJA",
        subtitle = "Kasuwa ta unguwa—daga titi zuwa ƙofa",
        enterMarket = "Shiga kasuwa",
        heroContentDescription = "Hoton ma'aikatan fili"
    ),
    navigation = NavigationStrings(
        cart = "Kwali",
        jobs = "Ayyuka",
        transporter = "Kasance mai isarwa"
    ),
    home = HomeStrings(addToCart = "Saka a kwali"),
    cart = CartStrings(
        storeLabel = "Shago",
        subtotalLabel = "Jimilla",
        extraFeeLabel = "Ƙarin kuɗin shago",
        checkout = "Biya",
        dialogTitle = "Ƙarin kuɗin jigila",
        accept = "Yarda",
        cancel = "Soke",
        methodLabels = mapOf(
            DeliveryMethod.BICYCLE to "Keke",
            DeliveryMethod.BIKE to "Babur",
            DeliveryMethod.CAR to "Mota"
        ),
        extraFeeMessage = { store -> "Ƙara kaya daga $store zai ƙara kuɗin mai isarwa. Ka yarda?" }
    ),
    jobs = JobsStrings(
        title = "Isarwa marasa mai karɓa",
        jobLabel = "Aiki",
        orderLabel = "Umarni",
        unclaimed = "Ba a karɓa ba",
        claimedBy = { courier -> "Ya koma $courier" },
        track = "Bibiyi",
        claim = "Karɓa"
    ),
    tracking = TrackingStrings(
        noTracking = { orderId -> "Babu bayanin bibiyi ga $orderId" },
        back = "Koma baya",
        pickup = "Dauka",
        dropoff = "Mika",
        courier = "Mai isarwa",
        placeholder = "Taswirar za ta bayyana idan ka saka maɓallin Maps",
        progress = { percent -> "Ci gaba: ${percent}%" }
    ),
    transporter = TransporterStrings(
        title = "Rajistar mai isarwa",
        submit = "Aika"
    ),
    vendor = VendorStrings(
        title = "Rajistar mai siyarwa",
        submit = "Aika"
    ),
    payments = PaymentsStrings(
        title = "Biya (Yanayin gwaji)",
        stagingHint = "Yi amfani da uwar garke na gwaji don Paystack ko Flutterwave.",
        paystackButton = "Biya da Paystack",
        flutterwaveButton = "Biya da Flutterwave",
        requireActivity = { gateway -> "Biyan $gateway na buƙatar mahallin Activity" },
        sdkMissing = { gateway -> "Ba a samu UI SDK na $gateway ba" },
        initError = { gateway -> "An kasa fara biyan $gateway" },
        verificationFailed = { gateway -> "Binciken $gateway ya kasa" },
        missingReference = { gateway -> "Ba a samu lambar bayanin mu'amala ta $gateway ba" },
        unexpectedResult = { gateway, code -> "$gateway ya dawo da lamba mara tsammani $code" },
        success = { gateway, reference -> "An yi nasarar biyan $gateway (ref: $reference)" },
        cancelled = { gateway -> "An soke biyan $gateway" },
        nqrTitle = "NQR (Lambar QR)",
        nqrButton = "Fitar da lambar NQR",
        qrHint = "Ka sa mai siya ya duba da manhajar bankinsa.",
        ussdTitle = "Madadin USSD",
        ussdButton = "Nemi lambar USSD",
        ussdHint = "Rubuta lambar a wayar bankinka sai ka tabbatar.",
        consentLabel = "Na amince da sanarwar sarrafa bayanai ta NDPA 2023.",
        consentDetails = "Za mu rubuta izinin biyan kuɗi bisa ka'idar NDPC GAID 2025.",
        referenceLabel = "Lambar bayanin",
        ussdCodeLabel = "Lambar USSD",
        bankLabel = "Banki",
        expiresLabel = "Ya ƙare a",
        nqrVerifyButton = "Tabbatar da biyan NQR",
        ussdVerifyButton = "Tabbatar da biyan USSD"
    )
)

private val igboStrings = AppStrings(
    languageLabel = "Asụsụ",
    themeLabel = "Agba",
    welcome = WelcomeStrings(
        title = "Nnọọ na OJA",
        subtitle = "Ahịa gị n'ógbè—site n'okporo ámá ruo n'ọnụ ụzọ",
        enterMarket = "Banye n'ahịa",
        heroContentDescription = "Foto ndị ọrụ ubi"
    ),
    navigation = NavigationStrings(
        cart = "Ngwugwu",
        jobs = "Ọrụ",
        transporter = "Bụ onye mbupu"
    ),
    home = HomeStrings(addToCart = "Tinye n'ngwugwu"),
    cart = CartStrings(
        storeLabel = "Ụlọ ahịa",
        subtotalLabel = "Ngụkọta pere mpe",
        extraFeeLabel = "Ọnụ ahịa ụlọ ọzọ",
        checkout = "Kwụọ ụgwọ",
        dialogTitle = "Ọnụ ahịa mbupu ọzọ",
        accept = "Kwere",
        cancel = "Kagbuo",
        methodLabels = mapOf(
            DeliveryMethod.BICYCLE to "Igwe igwe",
            DeliveryMethod.BIKE to "Okada",
            DeliveryMethod.CAR to "Ụgbọ ala"
        ),
        extraFeeMessage = { store -> "Ị na-etinyekwu ihe site n' $store ga-eme ka ụgwọ onye mbupu bawanye. Kwere?" }
    ),
    jobs = JobsStrings(
        title = "Nzipu enweghị onye",
        jobLabel = "Ọrụ",
        orderLabel = "Ihe a zụtara",
        unclaimed = "Enweghị onye",
        claimedBy = { courier -> "Were ya $courier" },
        track = "Lelee",
        claim = "Were"
    ),
    tracking = TrackingStrings(
        noTracking = { orderId -> "Enweghị nlele maka $orderId" },
        back = "Laghachi",
        pickup = "Nnata",
        dropoff = "Nyefee",
        courier = "Onye mbupu",
        placeholder = "Mapa ga-egosi mgbe ị tinyere igodo Maps",
        progress = { percent -> "Ọganihu: ${percent}%" }
    ),
    transporter = TransporterStrings(
        title = "Ndokwa onye mbupu",
        submit = "Zipụ"
    ),
    vendor = VendorStrings(
        title = "Ndokwa onye na-ere ahịa",
        submit = "Zipụ"
    ),
    payments = PaymentsStrings(
        title = "Ụgwọ (Ụdị nnwale)",
        stagingHint = "Jiri sava nnwale nyochaa Paystack ma ọ bụ Flutterwave.",
        paystackButton = "Kwụọ na Paystack",
        flutterwaveButton = "Kwụọ na Flutterwave",
        requireActivity = { gateway -> "Ụgwọ $gateway chọrọ ọnọdụ Activity" },
        sdkMissing = { gateway -> "A naghị ahụ UI SDK $gateway n'ime ngwa" },
        initError = { gateway -> "Enweghị ike ịmalite ụgwọ $gateway" },
        verificationFailed = { gateway -> "Nlele $gateway dara" },
        missingReference = { gateway -> "Enweghị ntụaka azụmahịa $gateway" },
        unexpectedResult = { gateway, code -> "$gateway zighachiri koodu a na-atụghị anya ya $code" },
        success = { gateway, reference -> "Ụgwọ $gateway gara nke ọma (ntụaka: $reference)" },
        cancelled = { gateway -> "E kagburu ụgwọ $gateway" },
        nqrTitle = "NQR (Koodu QR)",
        nqrButton = "Mee ka a hụ koodu NQR",
        qrHint = "Kwuo ka onye zụrụ jiri ngwa ụlọ akụ ha nyochaa ya.",
        ussdTitle = "Ndabere USSD",
        ussdButton = "Jụọ koodu USSD",
        ussdHint = "Tinye koodu ahụ na ngwa ụlọ akụ gị ma kwado.",
        consentLabel = "A kwenyere m na ịrụ ọrụ NDPA 2023.",
        consentDetails = "Anyị ga-edekọ nkwenye akwụmụgwọ dịka NDPC GAID 2025 siri kwuo.",
        referenceLabel = "Ntụaka",
        ussdCodeLabel = "Koodu USSD",
        bankLabel = "Ụlọ akụ",
        expiresLabel = "Na-agwụ na",
        nqrVerifyButton = "Lelee akwụmụgwọ NQR",
        ussdVerifyButton = "Lelee akwụmụgwọ USSD"
    )
)
