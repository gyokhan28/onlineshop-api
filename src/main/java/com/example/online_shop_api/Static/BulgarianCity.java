package com.example.online_shop_api.Static;

import com.example.online_shop_api.Entity.City;
import lombok.Getter;


@Getter
public enum BulgarianCity {
    SOFIA(1, "Sofia"),
    PLOVDIV(2, "Plovdiv"),
    VARNA(3, "Varna"),
    BURGAS(4, "Burgas"),
    RUSE(5, "Ruse"),
    STARA_ZAGORA(6, "Stara Zagora"),
    PLEVEN(7, "Pleven"),
    SLIVEN(8, "Sliven"),
    DOBRICH(9, "Dobrich"),
    SHUMEN(10, "Shumen"),
    PERNIK(11, "Pernik"),
    HASKOVO(12, "Haskovo"),
    YAMBOL(13, "Yambol"),
    PAZARDZHIK(14, "Pazardzhik"),
    BLAGEOVGRAD(15, "Blagoevgrad"),
    VELIKO_TARNOVO(16, "Veliko Tarnovo"),
    VRATSA(17, "Vratsa"),
    GABROVO(18, "Gabrovo"),
    VIDIN(19, "Vidin"),
    VRACA(20, "Vraca"),
    KYUSTENDIL(21, "Kyustendil"),
    TARGOVISHTE(22, "Targovishte"),
    MONTANA(23, "Montana"),
    SILISTRA(24, "Silistra"),
    DIMITROVGRAD(25, "Dimitrovgrad"),
    LOVECH(26, "Lovech"),
    KARDZHALI(27, "Kardzhali"),
    PETRICH(28, "Petrich"),
    SAMOKOV(29, "Samokov"),
    KAZANLAK(30, "Kazanlak"),
    TROYAN(31, "Troyan"),
    SVISHTOV(32, "Svishtov"),
    RAZGRAD(33, "Razgrad"),
    SANDANSKI(34, "Sandanski"),
    BALCHIK(35, "Balchik"),
    VELINGRAD(36, "Velingrad"),
    HARMANLI(37, "Harmanli"),
    DUPNITSA(38, "Dupnitsa"),
    PARVOMAY(39, "Parvomay"),
    KARLOVO(40, "Karlovo"),
    SLOVENGRAD(41, "Slovengrad"),
    AYTOS(42, "Aytos"),
    KAVARNA(43, "Kavarna"),
    NOVA_ZAGORA(44, "Nova Zagora"),
    GOTSE_DELTCHEV(45, "Gotse Delchev"),
    SIMITLI(46, "Simitli"),
    LOM(47, "Lom"),
    SEVLIEVO(48, "Sevlievo"),
    POLSKI_TRAMBESH(49, "Polski Trambesh"),
    TETEVEN(50, "Teteven"),
    SVILENGRAD(51, "Svilengrad"),
    BOTEVGRAD(52, "Botevgrad"),
    RADOMIR(53, "Radomir"),
    RAZLOG(54, "Razlog"),
    ELIN_PELIN(55, "Elin Pelin"),
    PESHTERA(56, "Peshtera"),
    CHIRPAN(57, "Chirpan"),
    KUBRAT(58, "Kubrat"),
    ALEKSANDROVGRAD(59, "Aleksandrograd"),
    PANAGYURISHTE(60, "Panagyurishte"),
    KOSTENETS(61, "Kostenets"),
    MOMCHILOVGRAD(62, "Momchilovgrad"),
    ASENUGLESHEN(63, "Asenugleshen"),
    SVETI_VLADIMIR(64, "Sveti Vladimir"),
    NESEBAR(65, "Nesebar"),
    DRYANOVO(66, "Dryanovo"),
    KOSTANDOVO(67, "Kostandovo"),
    POPOVO(68, "Popovo"),
    STRAZHITSA(69, "Strazhitsa"),
    TSAREVO(70, "Tsarevo"),
    TUTRAKAN(71, "Tutrakan"),
    ETROPOLE(72, "Etrople"),
    LUKOVIT(73, "Lukovit"),
    RADNEVO(74, "Radnevo"),
    ZAVET(75, "Zavet"),
    STRUMYANI(76, "Strumyani"),
    SOFIYAGRAD(77, "Sofiyagrad"),
    KNEZHA(78, "Knezha"),
    SHIVACHEVO(79, "Shivachevo"),
    DOLNICHIFLIK(80, "Dolnichiflik"),
    POMORIE(81, "Pomorie"),
    BREGOVO(82, "Bregovo"),
    SHABLA(83, "Shabla"),
    MALKO_TARNOWO(84, "Malko Tarnowo"),
    ISKAR(85, "Iskar"),
    BELI_POLETS(86, "Beli Polets"),
    SVIRACHI(87, "Svirachi"),
    DOBRICHKOVO(88, "Dobrichkovo"),
    TSAR_PETROVO(89, "Tsar Petrovo"),
    KUKLEN(90, "Kuklen"),
    KRICHIM(91, "Krichim"),
    LYASKOVETS(92, "Lyaskovets"),
    MEDKOVEC(93, "Medkovec"),
    MEDOVSK(94, "Medovsk"),
    BREZNITSA(95, "Breznitsa"),
    MEDNI_LOG(96, "Medni Log"),
    STARO_ORYAHOVO(97, "Staro Oryahovo");

    private final Long id;
    private final String name;

    BulgarianCity(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static City getCityById(Long cityId) {
        for (BulgarianCity city : BulgarianCity.values()) {
            if (city.id.equals(cityId)) {
                return City.builder().id(city.id).name(city.name).build();
            }
        }
        return null;
    }
}
