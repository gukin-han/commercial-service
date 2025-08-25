package com.loopers.common;

import java.math.*;
import java.sql.*;
import java.util.Random;

public class SeedBrandsAndProducts {

    static final String URL =
            "jdbc:mysql://localhost:3310/loopers"
                    + "?rewriteBatchedStatements=true"
                    + "&allowPublicKeyRetrieval=true"
                    + "&useSSL=false"
                    + "&serverTimezone=UTC";
    static final String USER = "application";
    static final String PASS = "application";

    static final int BRANDS   = 100;   // 생성할 브랜드 수
    static final int PRODUCTS = 1_000_000;  // 생성할 상품 수
    static final int BATCH    = 2_000;    // 배치 insert 크기
    static final long SEED    = 424242L;  // 랜덤 시드
    static final double ZIPF_S = 1.3;     // Zipf 분포 파라미터 (0이면 균등)

    public static void main(String[] args) throws Exception {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            conn.setAutoCommit(false);

            truncateTables(conn);
            insertBrands(conn);
            insertProducts(conn);

            System.out.println("✅ Seeding complete: " + BRANDS + " brands, " + PRODUCTS + " products");
        }
    }

    private static void truncateTables(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("SET FOREIGN_KEY_CHECKS=0");
            st.execute("TRUNCATE TABLE products");
            st.execute("TRUNCATE TABLE brands");
            st.execute("SET FOREIGN_KEY_CHECKS=1");
        }
    }

    private static void insertBrands(Connection conn) throws SQLException {
        String sql = "INSERT INTO brands (name, created_at, updated_at) VALUES (?, NOW(), NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= BRANDS; i++) {
                ps.setString(1, "brand-" + i);
                ps.addBatch();
                if (i % BATCH == 0) {
                    ps.executeBatch();
                    conn.commit();
                }
            }
            ps.executeBatch();
            conn.commit();
        }
        System.out.println("✅ Inserted brands: " + BRANDS);
    }

    private static void insertProducts(Connection conn) throws SQLException {
        String sql = "INSERT INTO products " +
                "(brand_id, name, like_count, status, stock_quantity, price, created_at, updated_at, deleted_at) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            Random rnd = new Random(SEED);
            ZipfSampler zipf = (ZIPF_S > 0) ? new ZipfSampler(BRANDS, ZIPF_S, SEED) : null;
            long now = System.currentTimeMillis() / 1000L;
            int in = 0;

            for (int i = 1; i <= PRODUCTS; i++) {
                long brandId = (zipf != null) ? zipf.sample() : 1 + rnd.nextInt(BRANDS);
                String name = "product-" + i;
                long likeCount = likeLognormal(rnd);
                String status = pickStatus(rnd);
                long stock = (long) Math.floor(Math.pow(rnd.nextDouble(), 2) * 100);
                BigDecimal price = bd(10 + rnd.nextDouble() * 990, 2);
                Timestamp created = new Timestamp((now - randRange(rnd, 0, 365L*24*3600)) * 1000);
                Timestamp updated = new Timestamp(created.getTime() + randRange(rnd, 0, 7L*24*3600) * 1000);
                Timestamp deleted = (rnd.nextDouble() < 0.02)
                        ? new Timestamp((now - randRange(rnd, 0, 365L*24*3600)) * 1000)
                        : null;

                int idx = 1;
                ps.setLong(idx++, brandId);
                ps.setString(idx++, name);
                ps.setLong(idx++, likeCount);
                ps.setString(idx++, status);
                ps.setLong(idx++, stock);
                ps.setBigDecimal(idx++, price);
                ps.setTimestamp(idx++, created);
                ps.setTimestamp(idx++, updated);
                if (deleted == null) ps.setNull(idx++, Types.TIMESTAMP);
                else ps.setTimestamp(idx++, deleted);

                ps.addBatch();
                if (++in == BATCH) {
                    ps.executeBatch();
                    conn.commit();
                    in = 0;
                }
            }
            if (in > 0) {
                ps.executeBatch();
                conn.commit();
            }
        }
        System.out.println("✅ Inserted products: " + PRODUCTS);
    }

    // --- 유틸 ---
    static BigDecimal bd(double v, int scale) {
        return BigDecimal.valueOf(v).setScale(scale, RoundingMode.HALF_UP);
    }
    static long randRange(Random r, long lo, long hi) {
        return lo + (long) Math.floor(r.nextDouble() * (hi - lo + 1));
    }
    // 목표 최대치(감각값): 20_000 근처로 맞추고 싶을 때
    static long likeLognormal(Random r) {
        // μ, σ는 분포 폭/중앙값 조절 파라미터
        double mu = Math.log(100);   // 중앙값 ~ e^μ
        double sigma = 1.0;          // 꼬리 두께 (0.8~1.4 조절)
        double x = Math.exp(mu + sigma * r.nextGaussian()); // 연속값
        // 소프트 캡: 큰 값은 완만히 눌러 중복 상단을 줄임
        double softCap = 20_000.0;
        double y = x <= softCap ? x : softCap + Math.log1p(x - softCap);
        // “은행가 반올림” 대신 확률적 반올림으로 타이 줄이기
        long floor = (long)Math.floor(y);
        double frac = y - floor;
        return floor + (r.nextDouble() < frac ? 1 : 0);
    }
    static String pickStatus(Random r) {
        double u = r.nextDouble();
        if (u < 0.01) return "SOLD_OUT";
        if (u < 0.05) return "STOPPED";
        return "ACTIVE";
    }

    // Zipf(=power-law) 연속 근사 역변환 샘플러
// 참고: F^{-1}(u) = floor( ((n^{1-s}-1)*u + 1)^{1/(1-s)} )
    static final class ZipfSampler {
        final int n; final double s; final Random rnd;
        ZipfSampler(int n, double s, long seed) { this.n=n; this.s=s; this.rnd=new Random(seed); }
        long sample() {
            double u = rnd.nextDouble();           // 0..1
            if (s == 1.0) {                        // s=1 특이점: 1/u 스케일 근사
                double x = Math.exp(u * Math.log(n));
                long k = (long)Math.floor(x);
                return clamp(k, 1, n);
            } else {
                double a = Math.pow(n, 1.0 - s) - 1.0;
                double x = Math.pow(a * u + 1.0, 1.0 / (1.0 - s));
                long k = (long)Math.floor(x);
                return clamp(k, 1, n);
            }
        }
        private static long clamp(long v, long lo, long hi) { return Math.max(lo, Math.min(hi, v)); }
    }
}
