package model;

public class UserProfile {
    private int userId;
    private String jurusan;
    private String fakultas;
    private String kontak;
    private String bio;
    private String linkedinUrl;
    private String instagramHandle;
    private String tiktokHandle;
    private String xHandle;
    private String youtubeUrl;
    private String otherSocial;
    
    public UserProfile() {}
    
    public UserProfile(int userId, String jurusan, String fakultas, String kontak, String bio,
                       String linkedinUrl, String instagramHandle, String tiktokHandle,
                       String xHandle, String youtubeUrl, String otherSocial) {
        this.userId = userId;
        this.jurusan = jurusan;
        this.fakultas = fakultas;
        this.kontak = kontak;
        this.bio = bio;
        this.linkedinUrl = linkedinUrl;
        this.instagramHandle = instagramHandle;
        this.tiktokHandle = tiktokHandle;
        this.xHandle = xHandle;
        this.youtubeUrl = youtubeUrl;
        this.otherSocial = otherSocial;
    }
    
    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getJurusan() { return jurusan; }
    public void setJurusan(String jurusan) { this.jurusan = jurusan; }
    
    public String getFakultas() { return fakultas; }
    public void setFakultas(String fakultas) { this.fakultas = fakultas; }
    
    public String getKontak() { return kontak; }
    public void setKontak(String kontak) { this.kontak = kontak; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }
    
    public String getInstagramHandle() { return instagramHandle; }
    public void setInstagramHandle(String instagramHandle) { this.instagramHandle = instagramHandle; }
    
    public String getTiktokHandle() { return tiktokHandle; }
    public void setTiktokHandle(String tiktokHandle) { this.tiktokHandle = tiktokHandle; }
    
    public String getXHandle() { return xHandle; }
    public void setXHandle(String xHandle) { this.xHandle = xHandle; }
    
    public String getYoutubeUrl() { return youtubeUrl; }
    public void setYoutubeUrl(String youtubeUrl) { this.youtubeUrl = youtubeUrl; }
    
    public String getOtherSocial() { return otherSocial; }
    public void setOtherSocial(String otherSocial) { this.otherSocial = otherSocial; }
}
