/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.core.locale;

import bisq.core.app.BisqEnvironment;

import bisq.asset.Asset;
import bisq.asset.AssetRegistry;
import bisq.asset.Token;
import bisq.asset.coins.BSQ;

import bisq.common.app.DevEnv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CurrencyUtil {

    private static final AssetRegistry assetRegistry = new AssetRegistry();

    private static String baseCurrencyCode = "BTC";

    public static void setBaseCurrencyCode(String baseCurrencyCode) {
        CurrencyUtil.baseCurrencyCode = baseCurrencyCode;
    }

    private static List<FiatCurrency> allSortedFiatCurrencies;

    private static List<FiatCurrency> createAllSortedFiatCurrenciesList() {
        Set<FiatCurrency> set = CountryUtil.getAllCountries().stream()
                .map(country -> getCurrencyByCountryCode(country.code))
                .collect(Collectors.toSet());
        List<FiatCurrency> list = new ArrayList<>(set);
        list.sort(TradeCurrency::compareTo);
        return list;
    }

    public static List<FiatCurrency> getAllSortedFiatCurrencies() {
        if (Objects.isNull(allSortedFiatCurrencies)) {
            allSortedFiatCurrencies = createAllSortedFiatCurrenciesList();
        }
        return allSortedFiatCurrencies;
    }


    public static List<FiatCurrency> getMainFiatCurrencies() {
        TradeCurrency defaultTradeCurrency = getDefaultTradeCurrency();
        List<FiatCurrency> list = new ArrayList<>();
        // Top traded currencies
        list.add(new FiatCurrency("USD"));
        list.add(new FiatCurrency("EUR"));
        list.add(new FiatCurrency("GBP"));
        list.add(new FiatCurrency("CAD"));
        list.add(new FiatCurrency("AUD"));
        list.add(new FiatCurrency("RUB"));
        list.add(new FiatCurrency("INR"));

        list.sort(TradeCurrency::compareTo);

        FiatCurrency defaultFiatCurrency = defaultTradeCurrency instanceof FiatCurrency ? (FiatCurrency) defaultTradeCurrency : null;
        if (defaultFiatCurrency != null && list.contains(defaultFiatCurrency)) {
            //noinspection SuspiciousMethodCalls
            list.remove(defaultTradeCurrency);
            list.add(0, defaultFiatCurrency);
        }
        return list;
    }

    private static List<CryptoCurrency> allSortedCryptoCurrencies;

    public static List<CryptoCurrency> getAllSortedCryptoCurrencies() {
        if (allSortedCryptoCurrencies == null)
            allSortedCryptoCurrencies = createAllSortedCryptoCurrenciesList();
        return allSortedCryptoCurrencies;
    }

    private static List<CryptoCurrency> createAllSortedCryptoCurrenciesList() {
        List<CryptoCurrency> result = assetRegistry.stream()
                .filter(CurrencyUtil::assetIsNotBaseCurrency)
                .filter(CurrencyUtil::excludeBsqUnlessDaoTradingIsActive)
                .map(CurrencyUtil::assetToCryptoCurrency)
                .collect(Collectors.toList());

        result.add(new CryptoCurrency("BURST", "Burstcoin"));
        result.add(new CryptoCurrency("XCP", "Counterparty"));
        result.add(new CryptoCurrency("DNET", "DarkNet"));
        result.add(new CryptoCurrency("DCR", "Decred"));
        result.add(new CryptoCurrency("DMC", "DynamicCoin"));
        result.add(new CryptoCurrency("ESP", "Espers"));
        result.add(new CryptoCurrency("ETC", "Ether Classic"));
        result.add(new CryptoCurrency("GRC", "Gridcoin"));
        result.add(new CryptoCurrency("LBC", "LBRY Credits"));
        result.add(new CryptoCurrency("LSK", "Lisk"));
        result.add(new CryptoCurrency("MAID", "MaidSafeCoin"));
        result.add(new CryptoCurrency("XMR", "Monero"));
        result.add(new CryptoCurrency("MT", "Mycelium Token", true));
        result.add(new CryptoCurrency("NAV", "Nav Coin"));
        result.add(new CryptoCurrency("NMC", "Namecoin"));
        result.add(new CryptoCurrency("NBT", "NuBits"));
        result.add(new CryptoCurrency("888", "OctoCoin"));
        result.add(new CryptoCurrency("PASC", "Pascal Coin", true));
        result.add(new CryptoCurrency("PEPECASH", "Pepe Cash"));
        result.add(new CryptoCurrency("POST", "PostCoin"));
        result.add(new CryptoCurrency("RDD", "ReddCoin"));
        result.add(new CryptoCurrency("REF", "RefToken", true));
        result.add(new CryptoCurrency("SFSC", "Safe FileSystem Coin"));
        result.add(new CryptoCurrency("SC", "Siacoin"));
        result.add(new CryptoCurrency("SF", "Siafund"));
        result.add(new CryptoCurrency("SIB", "Sibcoin"));
        result.add(new CryptoCurrency("STEEM", "STEEM"));

        result.add(new CryptoCurrency("UNO", "Unobtanium"));
        result.add(new CryptoCurrency("XZC", "Zcoin"));

        // Added 0.6.6
        result.add(new CryptoCurrency("STL", "Stellite"));
        result.add(new CryptoCurrency("DAI", "Dai Stablecoin", true));
        result.add(new CryptoCurrency("YTN", "Yenten"));
        result.add(new CryptoCurrency("DARX", "BitDaric"));
        result.add(new CryptoCurrency("ODN", "Obsidian"));
        result.add(new CryptoCurrency("CDT", "Cassubian Detk"));
        result.add(new CryptoCurrency("DGM", "DigiMoney"));
        result.add(new CryptoCurrency("SCS", "SpeedCash"));
        result.add(new CryptoCurrency("SOS", "SOS Coin", true));
        result.add(new CryptoCurrency("ACH", "AchieveCoin"));
        result.add(new CryptoCurrency("VDN", "vDinar"));
        result.add(new CryptoCurrency("WMCC", "WorldMobileCoin"));

        // Added 0.7.0
        result.add(new CryptoCurrency("ALC", "Angelcoin"));
        result.add(new CryptoCurrency("DIN", "Dinero"));
        result.add(new CryptoCurrency("NAH", "Strayacoin"));
        result.add(new CryptoCurrency("ROI", "ROIcoin"));
        result.add(new CryptoCurrency("RTO", "Arto"));
        result.add(new CryptoCurrency("KOTO", "Koto"));
        result.add(new CryptoCurrency("UBQ", "Ubiq"));
        result.add(new CryptoCurrency("QWARK", "Qwark", true));
        result.add(new CryptoCurrency("GEO", "GeoCoin", true));
        result.add(new CryptoCurrency("GRANS", "10grans", true));
        result.add(new CryptoCurrency("PHR", "Phore"));

        result.sort(TradeCurrency::compareTo);

        // Util for printing all altcoins for adding to FAQ page

       /* StringBuilder sb = new StringBuilder();
        result.stream().forEach(e -> sb.append("<li>&#8220;")
                .append(e.getCode())
                .append("&#8221;, &#8220;")
                .append(e.getName())
                .append("&#8221;</li>")
                .append("\n"));
        log.info(sb.toString());*/
        return result;
    }

    public static List<CryptoCurrency> getMainCryptoCurrencies() {
        final List<CryptoCurrency> result = new ArrayList<>();
        if (DevEnv.DAO_TRADING_ACTIVATED)
            result.add(new CryptoCurrency("BSQ", "BSQ"));
        if (!baseCurrencyCode.equals("BTC"))
            result.add(new CryptoCurrency("BTC", "Bitcoin"));
        if (!baseCurrencyCode.equals("DASH"))
            result.add(new CryptoCurrency("DASH", "Dash"));
        result.add(new CryptoCurrency("DCR", "Decred"));
        result.add(new CryptoCurrency("ETH", "Ether"));
        result.add(new CryptoCurrency("ETC", "Ether Classic"));
        result.add(new CryptoCurrency("GRC", "Gridcoin"));
        if (!baseCurrencyCode.equals("LTC"))
            result.add(new CryptoCurrency("LTC", "Litecoin"));
        result.add(new CryptoCurrency("XMR", "Monero"));
        result.add(new CryptoCurrency("MT", "Mycelium Token", true));
        result.add(new CryptoCurrency("NMC", "Namecoin"));
        result.add(new CryptoCurrency("SC", "Siacoin"));
        result.add(new CryptoCurrency("SF", "Siafund"));
        result.add(new CryptoCurrency("UNO", "Unobtanium"));
        result.add(new CryptoCurrency("ZEC", "Zcash"));
        result.sort(TradeCurrency::compareTo);

        return result;
    }


    /**
     * @return Sorted list of SEPA currencies with EUR as first item
     */
    private static Set<TradeCurrency> getSortedSEPACurrencyCodes() {
        return CountryUtil.getAllSepaCountries().stream()
                .map(country -> getCurrencyByCountryCode(country.code))
                .collect(Collectors.toSet());
    }

    // At OKPay you can exchange internally those currencies
    public static List<TradeCurrency> getAllOKPayCurrencies() {
        ArrayList<TradeCurrency> currencies = new ArrayList<>(Arrays.asList(
                new FiatCurrency("EUR"),
                new FiatCurrency("USD"),
                new FiatCurrency("GBP"),
                new FiatCurrency("CHF"),
                new FiatCurrency("RUB"),
                new FiatCurrency("PLN"),
                new FiatCurrency("JPY"),
                new FiatCurrency("CAD"),
                new FiatCurrency("AUD"),
                new FiatCurrency("CZK"),
                new FiatCurrency("NOK"),
                new FiatCurrency("SEK"),
                new FiatCurrency("DKK"),
                new FiatCurrency("HRK"),
                new FiatCurrency("HUF"),
                new FiatCurrency("NZD"),
                new FiatCurrency("RON"),
                new FiatCurrency("TRY"),
                new FiatCurrency("ZAR"),
                new FiatCurrency("HKD"),
                new FiatCurrency("CNY")
        ));
        currencies.sort(Comparator.comparing(TradeCurrency::getCode));
        return currencies;
    }

    // https://support.uphold.com/hc/en-us/articles/202473803-Supported-currencies
    public static List<TradeCurrency> getAllUpholdCurrencies() {
        ArrayList<TradeCurrency> currencies = new ArrayList<>(Arrays.asList(
                new FiatCurrency("USD"),
                new FiatCurrency("EUR"),
                new FiatCurrency("GBP"),
                new FiatCurrency("CNY"),
                new FiatCurrency("JPY"),
                new FiatCurrency("CHF"),
                new FiatCurrency("INR"),
                new FiatCurrency("MXN"),
                new FiatCurrency("AUD"),
                new FiatCurrency("CAD"),
                new FiatCurrency("HKD"),
                new FiatCurrency("NZD"),
                new FiatCurrency("SGD"),
                new FiatCurrency("KES"),
                new FiatCurrency("ILS"),
                new FiatCurrency("DKK"),
                new FiatCurrency("NOK"),
                new FiatCurrency("SEK"),
                new FiatCurrency("PLN"),
                new FiatCurrency("ARS"),
                new FiatCurrency("BRL"),
                new FiatCurrency("AED"),
                new FiatCurrency("PHP")
        ));

        currencies.sort(Comparator.comparing(TradeCurrency::getCode));
        return currencies;
    }

    //https://www.revolut.com/pa/faq#can-i-hold-multiple-currencies
    public static List<TradeCurrency> getAllRevolutCurrencies() {
        ArrayList<TradeCurrency> currencies = new ArrayList<>(Arrays.asList(
                new FiatCurrency("USD"),
                new FiatCurrency("GBP"),
                new FiatCurrency("EUR"),
                new FiatCurrency("PLN"),
                new FiatCurrency("CHF"),
                new FiatCurrency("DKK"),
                new FiatCurrency("NOK"),
                new FiatCurrency("SEK"),
                new FiatCurrency("RON"),
                new FiatCurrency("SGD"),
                new FiatCurrency("HKD"),
                new FiatCurrency("AUD"),
                new FiatCurrency("NZD"),
                new FiatCurrency("TRY"),
                new FiatCurrency("ILS"),
                new FiatCurrency("AED"),
                new FiatCurrency("CAD"),
                new FiatCurrency("HUF"),
                new FiatCurrency("INR"),
                new FiatCurrency("JPY"),
                new FiatCurrency("MAD"),
                new FiatCurrency("QAR"),
                new FiatCurrency("THB"),
                new FiatCurrency("ZAR")
        ));

        currencies.sort(Comparator.comparing(TradeCurrency::getCode));
        return currencies;
    }

    public static boolean isFiatCurrency(String currencyCode) {
        try {
            return currencyCode != null && !currencyCode.isEmpty() && !isCryptoCurrency(currencyCode) && Currency.getInstance(currencyCode) != null;
        } catch (Throwable t) {
            return false;
        }
    }

    public static Optional<FiatCurrency> getFiatCurrency(String currencyCode) {
        return getAllSortedFiatCurrencies().stream().filter(e -> e.getCode().equals(currencyCode)).findAny();
    }

    @SuppressWarnings("WeakerAccess")
    public static boolean isCryptoCurrency(String currencyCode) {
        return getCryptoCurrency(currencyCode).isPresent();
    }

    public static Optional<CryptoCurrency> getCryptoCurrency(String currencyCode) {
        return getAllSortedCryptoCurrencies().stream().filter(e -> e.getCode().equals(currencyCode)).findAny();
    }

    public static Optional<TradeCurrency> getTradeCurrency(String currencyCode) {
        Optional<FiatCurrency> fiatCurrencyOptional = getFiatCurrency(currencyCode);
        if (isFiatCurrency(currencyCode) && fiatCurrencyOptional.isPresent()) {
            return Optional.of(fiatCurrencyOptional.get());
        } else {
            Optional<CryptoCurrency> cryptoCurrencyOptional = getCryptoCurrency(currencyCode);
            if (isCryptoCurrency(currencyCode) && cryptoCurrencyOptional.isPresent()) {
                return Optional.of(cryptoCurrencyOptional.get());
            } else {
                return Optional.<TradeCurrency>empty();
            }
        }
    }


    public static FiatCurrency getCurrencyByCountryCode(String countryCode) {
        if (countryCode.equals("XK"))
            return new FiatCurrency("EUR");
        else
            return new FiatCurrency(Currency.getInstance(new Locale(LanguageUtil.getDefaultLanguage(), countryCode)).getCurrencyCode());
    }


    public static String getNameByCode(String currencyCode) {
        if (isCryptoCurrency(currencyCode))
            return getCryptoCurrency(currencyCode).get().getName();
        else
            try {
                return Currency.getInstance(currencyCode).getDisplayName();
            } catch (Throwable t) {
                log.debug("No currency name available " + t.getMessage());
                return currencyCode;
            }
    }


    public static String getNameAndCode(String currencyCode) {
        return getNameByCode(currencyCode) + " (" + currencyCode + ")";
    }

    public static TradeCurrency getDefaultTradeCurrency() {
        return GlobalSettings.getDefaultTradeCurrency();
    }

    private static boolean assetIsNotBaseCurrency(Asset asset) {
        return !asset.getTickerSymbol().equals(baseCurrencyCode);
    }

    private static CryptoCurrency assetToCryptoCurrency(Asset asset) {
        return new CryptoCurrency(asset.getTickerSymbol(), asset.getName(), asset instanceof Token);
    }

    private static boolean excludeBsqUnlessDaoTradingIsActive(Asset asset) {
        return (!(asset instanceof BSQ) || (DevEnv.DAO_TRADING_ACTIVATED
                && ((BSQ) asset).getNetwork().name().equals(BisqEnvironment.getBaseCurrencyNetwork().getNetwork())));
    }
}
