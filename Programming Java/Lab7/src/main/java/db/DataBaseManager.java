package main.java.db;

import main.java.Model.Address;
import main.java.Model.Coordinates;
import main.java.Model.Organization;
import main.java.Model.OrganizationDraft;
import main.java.Model.OrganizationType;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class DataBaseManager {
    private static final String URL = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/studs");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "s467185");
    private static final String PASS = System.getenv().getOrDefault("DB_PASS", "pTZvu82P0YIHlOwD");

    private static Connection connection;

    private static final String SQL_FIND_USER_BY_LOGIN =
            "SELECT id, login, pass_md2_hex FROM app_user WHERE login = ?";

    private static final String SQL_INSERT_USER =
            "INSERT INTO app_user(login, pass_md2_hex) VALUES (?,?) RETURNING id";

    private static final String SQL_SELECT_ALL_ORG = """
            SELECT id,name,x,y,annual_turnover,employees_count,type,street,zip_code,creation_date,owner_id
            FROM organization ORDER BY id
            """;

    private static final String SQL_INSERT_ORG = """
            INSERT INTO organization(name,x,y,annual_turnover,employees_count,type,street,zip_code,owner_id)
            VALUES (?,?,?,?,?,?,?,?,?) RETURNING id, creation_date
            """;

    private static final String SQL_UPDATE_ORG = """
            UPDATE organization
            SET name=?, x=?, y=?, annual_turnover=?, employees_count=?, type=?, street=?, zip_code=?
            WHERE id=? AND owner_id=?
            """;

    private static final String SQL_DELETE_ORG =
            "DELETE FROM organization WHERE id=? AND owner_id=?";

    private static final String SQL_SELECT_HEAD = """
            SELECT id,name,x,y,annual_turnover,employees_count,type,street,zip_code,creation_date,owner_id
            FROM organization
            ORDER BY employees_count ASC, id ASC
            LIMIT 1
            """;


    private static final String SQL_DELETE_ALL_BY_OWNER =
            "DELETE FROM organization WHERE owner_id=?";

    public static void connect() {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("[DB] url=" + URL +
                    ", user=" + USER +
                    ", passLen=" + (PASS == null ? -1 : PASS.length()) +
                    (URL.contains("sslmode") ? ", ssl=explicit" : ", ssl=default"));
            connection = DriverManager.getConnection(URL, USER, PASS);
            connection.setAutoCommit(true);
            System.out.println("[DB] Connected to " + URL);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("[DB] Connection error: " + e.getMessage(), e);
        }
    }

    public static void closeQuiet() {
        try {
            if (connection != null) connection.close();
        } catch (Exception ignore) {
        }
    }


    public static long register(String login, String rawPassword) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT_USER)) {
            ps.setString(1, login);
            ps.setString(2, md2Hex(rawPassword));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    public static Optional<Long> authenticate(String login, String rawPassword) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_FIND_USER_BY_LOGIN)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                String dbHash = rs.getString("pass_md2_hex");
                return md2Hex(rawPassword).equalsIgnoreCase(dbHash)
                        ? Optional.of(rs.getLong("id")) : Optional.empty();
            }
        }
    }


    public static List<Organization> loadAllOrganizations() throws SQLException {
        List<Organization> out = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_ALL_ORG);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapRowToOrganization(rs));
        }
        return out;
    }

    public static Organization insertOrganization(OrganizationDraft d, long ownerId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT_ORG)) {
            ps.setString(1, d.name);
            ps.setInt(2, d.coordinates.getX());
            ps.setLong(3, d.coordinates.getY());
            if (d.annualTurnover == null) ps.setNull(4, Types.INTEGER);
            else ps.setInt(4, d.annualTurnover);
            ps.setInt(5, d.employeesCount);
            ps.setString(6, d.type.name());
            ps.setString(7, d.officialAddress == null ? null : d.officialAddress.getStreet());
            ps.setString(8, d.officialAddress == null ? null : d.officialAddress.getZipCode());
            ps.setLong(9, ownerId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                long id = rs.getLong("id");
                Timestamp ts = rs.getTimestamp("creation_date");
                ZonedDateTime created = (ts != null)
                        ? ts.toInstant().atZone(ZoneId.systemDefault())
                        : ZonedDateTime.now();
                return new Organization(id, created, d.name, d.coordinates, d.annualTurnover,
                        d.employeesCount, d.type, d.officialAddress);
            }
        }
    }

    public static boolean updateOrganization(Organization o, long ownerId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE_ORG)) {
            ps.setString(1, o.getName());
            ps.setInt(2, o.getCoordinates().getX());
            ps.setLong(3, o.getCoordinates().getY());
            if (o.getAnnualTurnover() == null) ps.setNull(4, Types.INTEGER);
            else ps.setInt(4, o.getAnnualTurnover());
            ps.setInt(5, o.getEmployeesCount());
            ps.setString(6, o.getType().name());
            ps.setString(7, o.getOfficialAddress() == null ? null : o.getOfficialAddress().getStreet());
            ps.setString(8, o.getOfficialAddress() == null ? null : o.getOfficialAddress().getZipCode());
            ps.setLong(9, o.getId());
            ps.setLong(10, ownerId);
            return ps.executeUpdate() == 1;
        }
    }

    public static boolean deleteOrganization(long id, long ownerId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE_ORG)) {
            ps.setLong(1, id);
            ps.setLong(2, ownerId);
            return ps.executeUpdate() == 1;
        }
    }

    public static Optional<OrgWithOwner> findHead() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_HEAD);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) return Optional.empty();
            Organization org = mapRowToOrganization(rs);
            long ownerId = rs.getLong("owner_id");
            return Optional.of(new OrgWithOwner(org, ownerId));
        }
    }

    private static final String SQL_FIND_ANY_BY_OWNER_AND_EMP = """
            SELECT id FROM organization
            WHERE owner_id = ? AND employees_count = ?
            ORDER BY id
            LIMIT 1
            """;

    public static Optional<Long> findAnyByOwnerAndEmployees(long ownerId, int employeesCount) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_FIND_ANY_BY_OWNER_AND_EMP)) {
            ps.setLong(1, ownerId);
            ps.setInt(2, employeesCount);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(rs.getLong(1));
            }
        }
    }


    public static int deleteAllByOwner(long ownerId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE_ALL_BY_OWNER)) {
            ps.setLong(1, ownerId);
            return ps.executeUpdate();
        }
    }


    public static final class OrgWithOwner {
        public final Organization org;
        public final long ownerId;

        public OrgWithOwner(Organization org, long ownerId) {
            this.org = org;
            this.ownerId = ownerId;
        }
    }

    private static Organization mapRowToOrganization(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        int x = rs.getInt("x");
        long y = rs.getLong("y");
        Integer annual = (Integer) rs.getObject("annual_turnover");
        int employees = rs.getInt("employees_count");
        OrganizationType type = OrganizationType.valueOf(rs.getString("type"));
        String street = rs.getString("street");
        String zip = rs.getString("zip_code");
        Timestamp ts = rs.getTimestamp("creation_date");
        ZonedDateTime created = (ts != null)
                ? ts.toInstant().atZone(ZoneId.systemDefault())
                : ZonedDateTime.now();

        Coordinates coord = new Coordinates(x, y);
        Address addr = (street == null && zip == null) ? null : new Address(street, zip);
        return new Organization(id, created, name, coord, annual, employees, type, addr);
    }

    public static String md2Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD2");
            byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(d.length * 2);
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD2 not available", e);
        }
    }

    private DataBaseManager() {
    }
}
