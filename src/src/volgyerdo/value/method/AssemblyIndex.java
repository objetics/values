/*
 * To change this license header, 
choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import volgyerdo.value.structure.Value;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class AssemblyIndex implements Value {

    @Override
    public String name() {
        return "Assembly index";
    }

    /**
     * Számolja egy byte[] tömb assembly indexét (összeállítási indexét). Az
     * assembly index azt mutatja, hogy a tömb legkisebb lépésszámmal hogyan
     * állítható össze, ha bármely korábban elkészített rész újra
     * felhasználható.
     *
     * @param arr bemeneti byte tömb
     * @return a tömb assembly indexe (minimális lépésszám)
     */
    @Override
    public double value(byte[] arr) {
        int n = arr.length;
        if (n <= 1) {
            // Ha a tömb hossza 0 vagy 1, nem szükséges lépés (alap építőkocka ingyen van).
            return 0;
        }
        // dp[i] = minimális lépésszám az első i elem (arr[0..i-1]) összeállításához
        int[] dp = new int[n + 1];
        Arrays.fill(dp, Integer.MAX_VALUE / 2);
        dp[0] = 0;
        dp[1] = 0; // az első byte építőkocka lépés nélkül elérhető

        // Előfeldolgozás: gördülő hash-ek kiszámítása két modullal (ütközés csökkentése)
        int base = 257;
        long mod1 = 1000000007L, mod2 = 1000000009L;
        long[] hash1 = new long[n + 1], hash2 = new long[n + 1];
        long[] pow1 = new long[n + 1], pow2 = new long[n + 1];
        pow1[0] = 1;
        pow2[0] = 1;
        for (int i = 0; i < n; i++) {
            int val = (arr[i] & 0xFF);
            hash1[i + 1] = (hash1[i] * base + val) % mod1;
            hash2[i + 1] = (hash2[i] * base + val) % mod2;
            pow1[i + 1] = (pow1[i] * base) % mod1;
            pow2[i + 1] = (pow2[i] * base) % mod2;
        }

        for (int j = 1; j < n; j++) {
            // 1) Új byte hozzáadása a (j-1)-hosszú prefixhez
            dp[j + 1] = Math.min(dp[j + 1], dp[j] + 1);

            // 2) Korábbi rész újrahasználata: keressük az arr[j..j+L-1] rész maximális L hosszát,
            //    ami már előfordul arr[0..j-1]-ben.
            int lo = 1, hi = n - j, best = 0;
            while (lo <= hi) {
                int mid = (lo + hi) / 2;
                // Hash kiszámolása az arr[j..j+mid-1] részre
                long subHash1 = (hash1[j + mid] - hash1[j] * pow1[mid] % mod1 + mod1) % mod1;
                long subHash2 = (hash2[j + mid] - hash2[j] * pow2[mid] % mod2 + mod2) % mod2;
                boolean found = false;
                // Ellenőrizze, hogy ez a részhol szerepel-e a prefixben
                for (int k = 0; k + mid <= j; k++) {
                    long preHash1 = (hash1[k + mid] - hash1[k] * pow1[mid] % mod1 + mod1) % mod1;
                    long preHash2 = (hash2[k + mid] - hash2[k] * pow2[mid] % mod2 + mod2) % mod2;
                    if (subHash1 == preHash1 && subHash2 == preHash2) {
                        // Talált egyező hash, ellenőrizzük ténylegesen is a byte-okat
                        boolean match = true;
                        for (int x = 0; x < mid; x++) {
                            if (arr[j + x] != arr[k + x]) {
                                match = false;
                                break;
                            }
                        }
                        if (match) {
                            found = true;
                            break;
                        }
                    }
                }
                if (found) {
                    // Ha megtaláltuk, megnöveljük az alsó határt
                    best = mid;
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
            }
            // Ha van ismételhető hossz (best > 0), akkor 1..best hosszúságban is összefűzhetünk
            for (int L = 1; L <= best; L++) {
                int i = j + L;
                dp[i] = Math.min(dp[i], dp[j] + 1);
            }
        }
        return dp[n];
    }

    /**
     * Számolja egy tetszőleges Collection assembly indexét. Az egyes elemek
     * "alapelemként" szerepelnek. A cél: minimális lépésszám, ahol lépésenként
     * bármely már létrehozott részt újrahasználhatsz.
     *
     * @param collection
     * @return assembly index (minimális lépésszám)
     */
    @Override
    public double value(Collection collection) {
        List list = new ArrayList(collection);
        int n = list.size();
        if (n <= 1) {
            return 0;
        }
        int[] dp = new int[n + 1];
        Arrays.fill(dp, Integer.MAX_VALUE / 2);
        dp[0] = 0;
        dp[1] = 0; // első elem "adott"

        // Minden részlista hash-ét eltároljuk: Map<Integer, List<Integer>>: hash -> startindexek listája
        Map<Integer, List<Integer>> prefixMap = new HashMap<>();

        for (int i = 0; i < n; i++) {
            // Új elem hozzáadása
            dp[i + 1] = Math.min(dp[i + 1], dp[i] + 1);

            // Prefix hash frissítése (a 0...i közti prefixek)
            for (int len = 1; len <= i; len++) {
                List sub = list.subList(i, i + len > n ? n : i + len);
                if (sub.size() < len) {
                    break;
                }
                int hash = sub.hashCode();
                List<Integer> indices = prefixMap.get(hash);
                if (indices != null) {
                    // Végigpróbáljuk, hogy a prefixben valóban van-e egyező részlista
                    for (int idx : indices) {
                        if (list.subList(idx, idx + len).equals(sub)) {
                            // Megtaláltuk: az i+len-ig tartó prefix DP-je is frissíthető
                            dp[i + len] = Math.min(dp[i + len], dp[i] + 1);
                            // Minden hosszra csak egyszer frissítünk, elég megtalálni az első egyezést
                            break;
                        }
                    }
                }
            }

            // Minden lehetséges részlistát, ami véget ér i-nél, felvesszük a prefixMap-be
            for (int start = 0; start <= i; start++) {
                List sub = list.subList(start, i + 1);
                int hash = sub.hashCode();
                prefixMap.computeIfAbsent(hash, k -> new ArrayList<>()).add(start);
            }
        }
        return dp[n];
    }

}
