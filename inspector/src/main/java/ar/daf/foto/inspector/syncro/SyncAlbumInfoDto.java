package ar.daf.foto.inspector.syncro;

import org.joda.time.DateTime;

import ar.daf.foto.inspector.model.AlbumInfo;

public class SyncAlbumInfoDto {
	
	private int versionMayor;
	private int versionMenor;
	private int versionRevision;
	
	private String hashId;
	private DateTime fechaActualizacion;
	private String contentHash;

	public static SyncAlbumInfoDto fromAlbum(AlbumInfo albumInfo) {
		SyncAlbumInfoDto result = null;
		if (albumInfo != null) {
			result = new SyncAlbumInfoDto();
			result.setVersionMayor(albumInfo.getVersionMayor());
			result.setVersionMenor(albumInfo.getVersionMenor());
			result.setVersionRevision(albumInfo.getVersionRevision());
			result.setHashId(albumInfo.getHashId());
			result.setFechaActualizacion(albumInfo.getFechaActualizacion());
			result.setContentHash(albumInfo.getContentHash());
		}
		return result;
	}
	
	public String getHashId() {
		return hashId;
	}
	public void setHashId(String hashId) {
		this.hashId = hashId;
	}
	public DateTime getFechaActualizacion() {
		return fechaActualizacion;
	}
	public void setFechaActualizacion(DateTime fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}
	public String getContentHash() {
		return contentHash;
	}
	public void setContentHash(String contentHash) {
		this.contentHash = contentHash;
	}
	public int getVersionMayor() {
		return versionMayor;
	}
	public void setVersionMayor(int versionMayor) {
		this.versionMayor = versionMayor;
	}
	public int getVersionMenor() {
		return versionMenor;
	}
	public void setVersionMenor(int versionMenor) {
		this.versionMenor = versionMenor;
	}
	public int getVersionRevision() {
		return versionRevision;
	}
	public void setVersionRevision(int versionRevision) {
		this.versionRevision = versionRevision;
	}	
}