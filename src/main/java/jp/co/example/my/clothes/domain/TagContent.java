package jp.co.example.my.clothes.domain;

/**
 * タグ内容情報を扱うドメインクラス.
 * 
 * @author ashibe
 *
 */
public class TagContent {

	/**
	 * タグ内容のID
	 */
	private Integer id;

	/**
	 * タグ内容
	 */
	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "TagContent [id=" + id + ", name=" + name + "]";
	}

}