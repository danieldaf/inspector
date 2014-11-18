package ar.daf.foto.inspector.album;

import org.joda.time.DateTime;

import ar.daf.foto.inspector.model.AlbumInfo;

public class AlbumInfoFile {
	
	/**
	 * Los campos versionMayor, versionMenor y versionRevision indican el nro de version de la base de datos
	 */
	private int versionMayor;
	private int versionMenor;
	private int versionRevision;
	/**
	 * Este campo permite identificar univocamente al album, se arma en base al path relativo y el nombre de la carpeta del album.
	 */
	private String hashId;
	
	public static AlbumInfoFile fromAlbumInfo(AlbumInfo albumInfo) {
		AlbumInfoFile result = null;
		if (albumInfo != null) {
			result = new AlbumInfoFile();
			result.setVersionMayor(albumInfo.getVersionMayor());
			result.setVersionMenor(albumInfo.getVersionMenor());
			result.setVersionRevision(albumInfo.getVersionRevision());
			result.setHashId(albumInfo.getHashId());
			result.setFechaActualizacion(albumInfo.getFechaActualizacion());
			result.setContentHash(albumInfo.getContentHash());
		}
		return result;
	}
	
	public static AlbumInfo toAlbumInfo(AlbumInfoFile albumInfo) {
		AlbumInfo result = null;
		if (albumInfo != null) {
			result = new AlbumInfo();
			result.setVersionMayor(albumInfo.getVersionMayor());
			result.setVersionMenor(albumInfo.getVersionMenor());
			result.setVersionRevision(albumInfo.getVersionRevision());
			result.setHashId(albumInfo.getHashId());
			result.setFechaActualizacion(albumInfo.getFechaActualizacion());
			result.setContentHash(albumInfo.getContentHash());
		}
		return result;
	}

	/**
	 * Este campo contiene la fecha de actualizacion de la carpeta que contiene el album.
	 * Se emplea para determinar con rapidez si es necesario o no actualizar el album.
	 */
	private DateTime fechaActualizacion;
	/**
	 * Este campo cotiene un hash usando SHA1 del archivo de texto (json) del album.
	 * El hash se calcula poniendo el campo hashContent y fechaActualizacion en null.
	 */
	private String contentHash;

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
