package dao;

import model.UserProfile;
import database.DBConnection;

import java.sql.*;

public class UserProfileDAO {
    
    public UserProfile getProfileByUserId(long userId) {
        String query = "SELECT * FROM user_profiles WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new UserProfile(
                    rs.getLong("user_id"),
                    rs.getString("jurusan"),
                    rs.getString("fakultas"),
                    rs.getString("kontak"),
                    rs.getString("bio"),
                    rs.getString("linkedin_url"),
                    rs.getString("instagram_handle"),
                    rs.getString("tiktok_handle"),
                    rs.getString("x_handle"),
                    rs.getString("youtube_url"),
                    rs.getString("other_social")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean saveOrUpdateProfile(UserProfile profile) {
        // Ensure profile row exists
        String checkQuery = "SELECT user_id FROM user_profiles WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            
            checkStmt.setLong(1, profile.getUserId());
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // UPDATE
                String updateQuery = "UPDATE user_profiles SET jurusan = ?, fakultas = ?, kontak = ?, bio = ?, " +
                                     "linkedin_url = ?, instagram_handle = ?, tiktok_handle = ?, x_handle = ?, " +
                                     "youtube_url = ?, other_social = ? WHERE user_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                    pstmt.setString(1, profile.getJurusan());
                    pstmt.setString(2, profile.getFakultas());
                    pstmt.setString(3, profile.getKontak());
                    pstmt.setString(4, profile.getBio());
                    pstmt.setString(5, profile.getLinkedinUrl());
                    pstmt.setString(6, profile.getInstagramHandle());
                    pstmt.setString(7, profile.getTiktokHandle());
                    pstmt.setString(8, profile.getXHandle());
                    pstmt.setString(9, profile.getYoutubeUrl());
                    pstmt.setString(10, profile.getOtherSocial());
                    pstmt.setLong(11, profile.getUserId());
                    return pstmt.executeUpdate() > 0;
                }
            } else {
                // INSERT
                String insertQuery = "INSERT INTO user_profiles (user_id, jurusan, fakultas, kontak, bio, " +
                                     "linkedin_url, instagram_handle, tiktok_handle, x_handle, youtube_url, other_social) " +
                                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                    pstmt.setLong(1, profile.getUserId());
                    pstmt.setString(2, profile.getJurusan());
                    pstmt.setString(3, profile.getFakultas());
                    pstmt.setString(4, profile.getKontak());
                    pstmt.setString(5, profile.getBio());
                    pstmt.setString(6, profile.getLinkedinUrl());
                    pstmt.setString(7, profile.getInstagramHandle());
                    pstmt.setString(8, profile.getTiktokHandle());
                    pstmt.setString(9, profile.getXHandle());
                    pstmt.setString(10, profile.getYoutubeUrl());
                    pstmt.setString(11, profile.getOtherSocial());
                    return pstmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
