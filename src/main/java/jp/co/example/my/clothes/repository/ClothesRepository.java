package jp.co.example.my.clothes.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import jp.co.example.my.clothes.domain.Brand;
import jp.co.example.my.clothes.domain.Category;
import jp.co.example.my.clothes.domain.Clothes;
import jp.co.example.my.clothes.domain.Color;
import jp.co.example.my.clothes.domain.Size;
import jp.co.example.my.clothes.domain.MonthlyDataDTO;
import jp.co.example.my.clothes.domain.TagContent;

/**
 * clothesを扱うためのレポジトリ.
 * 
 * @author ashibe
 *
 */
@Repository
public class ClothesRepository {

	@Autowired
	private NamedParameterJdbcTemplate template;

	private static final RowMapper<Clothes> CLOTHES_ROW_MAPPER = new BeanPropertyRowMapper<>(Clothes.class);
	private static final RowMapper<Brand> BRAND_ROW_MAPPER = new BeanPropertyRowMapper<>(Brand.class);
	private static final RowMapper<TagContent> TAG_CONTENTS_ROW_MAPPER = new BeanPropertyRowMapper<>(TagContent.class);
	private static final RowMapper<MonthlyDataDTO> DATA_ROW_MAPPER = new BeanPropertyRowMapper<>(MonthlyDataDTO.class);

	/**
	 * カレンダー画面で使用するローマッパー.
	 */
	private static final RowMapper<Clothes> CLOTHES_ROW_MAPPER4 = (rs,i) ->{
		
		Clothes clothes = new Clothes();
		clothes.setId(rs.getInt("cl_id"));
		clothes.setUserId(rs.getInt("cl_user_id"));		
		clothes.setCategoryId(rs.getInt("cl_category_id"));
		clothes.setBrandId(rs.getInt("cl_brand_id"));
		clothes.setImagePath(rs.getString("cl_image_path"));
		clothes.setPrice(rs.getInt("price"));
		clothes.setColorId(rs.getInt("cl_color_id"));
		clothes.setSeason(rs.getString("cl_season"));
		clothes.setSizeId(rs.getInt("cl_size_id"));
		clothes.setPerchaseDate(rs.getDate("cl_perchase_date"));
		clothes.setComment(rs.getString("cl_comment"));
		clothes.setDeleted(rs.getBoolean("cl_deleted"));
		Category category = new Category();
		category.setId(rs.getInt("ca_id"));
		category.setName(rs.getString("ca_name"));
		clothes.setCategory(category);
		return clothes;
		
	};

	private static final String SQL = "SELECT id,user_id,category_id,brand_id,image_path,price,color_id,season,size_id,perchase_date,comment,deleted ";

	/**
	 * 新規にアイテム情報をインサート.
	 * 
	 * @param clothes
	 */
	public void insertClothes(Clothes clothes) {
		String sql = "insert into clothes(user_id,image_path,price,category_id,brand_id, color_id,size_id,season,Perchase_date,comment)values(:userId,:imagePath,:price, :categoryId,:brandId,:colorId,:sizeId,:season,:PerchaseDate,:comment)";
		SqlParameterSource param = new BeanPropertySqlParameterSource(clothes);
		template.update(sql, param);

	}

	/**
	 * 登録アイテム一覧をID順で取得します.
	 * 
	 * @param ログインユーザID
	 * @return 登録アイテム一覧
	 */
	public List<Clothes> findAll(Integer userId) {
		String sql = SQL + "FROM clothes WHERE user_id=:userId ORDER BY id;";
		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId);
		List<Clothes> clothesList = template.query(sql, param, CLOTHES_ROW_MAPPER);
		return clothesList;
	}
	
	/**
	 * カテゴリー名付きの登録アイテム一覧をでID順で取得します.
	 * 
	 * @param userId ログインユーザID
	 * @return　登録アイテム一覧
	 */
	public List<Clothes> findAllWithCategory(Integer userId){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT cl.id cl_id,cl.user_id cl_user_id,cl.category_id cl_category_id,cl.brand_id cl_brand_id,cl.image_path cl_image_path,");
		sql.append("CASE WHEN cl.price IS NULL THEN 0 ELSE cl.price END,cl.color_id cl_color_id,");
		sql.append("cl.season cl_season,cl.size_id cl_size_id,cl.perchase_date cl_perchase_date,cl.comment cl_comment,cl.deleted cl_deleted,");
		sql.append("ca.id ca_id,ca.name ca_name FROM clothes cl INNER JOIN categories ca ");
		sql.append("ON cl.category_id=ca.id ");
		sql.append("WHERE cl.user_id=:userId AND cl.deleted='FALSE' ORDER BY cl.id;");
		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId);
		List<Clothes> clothesList = template.query(sql.toString(), param, CLOTHES_ROW_MAPPER4);
		return clothesList;
	}

	/**
	 * 登録アイテムをカテゴリごとに分けて表示します.
	 * 
	 * @param userId     ログインユーザID
	 * @param categoryId カテゴリID
	 * @return 登録アイテム一覧
	 */
	public List<Clothes> findByCategory(Integer userId, Integer categoryId) {
		String sql = SQL + "FROM clothes WHERE user_id=:userId AND category_id=:categoryId ORDER BY id;";
		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId).addValue("categoryId",
				categoryId);
		List<Clothes> clothesList = template.query(sql, param, CLOTHES_ROW_MAPPER);
		return clothesList;
	}

	/**
	 * 登録アイテムをカテゴリごとに分けて表示します.
	 * 
	 * @param userId     ログインユーザID
	 * @param categoryId カテゴリID
	 * @return 登録アイテム一覧
	 */
	public List<Clothes> findByCategoryForCoordinate(Integer userId, Integer categoryId) {
		String sql = SQL + "FROM clothes WHERE user_id=:userId AND category_id=:categoryId ORDER BY id;";
		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId).addValue("categoryId",
				categoryId);
		List<Clothes> clothesList = template.query(sql, param, CLOTHES_ROW_MAPPER);

		if (clothesList.size() == 0) {
			return Collections.emptyList();
		}
		return clothesList;
	}

	/**
	 * 登録しているブランド名を検索します.
	 * 
	 * @param userId ログインユーザID
	 * @return ブランド名の入ったリスト
	 */
	public List<Brand> showBrandName(Integer userId) {
		String sql = "SELECT DISTINCT brands.id,brands.name FROM brands INNER JOIN clothes ON brands.id = clothes.brand_id WHERE clothes.user_id=:userId AND deleted = false;";
		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId);
		List<Brand> brandList = template.query(sql, param, BRAND_ROW_MAPPER);
		return brandList;
	}

	/**
	 * 登録アイテムをブランド別に分けて表示します.
	 * 
	 * @param userId  ログインユーザID
	 * @param brandId ブランドID
	 * @return 登録アイテム一覧
	 */
	public List<Clothes> findByBrand(Integer userId, Integer brandId) {
		String sql = SQL + "FROM clothes WHERE user_id=:userId AND brand_id=:brandId  AND deleted =false ORDER BY id;";
		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId).addValue("brandId", brandId);
		List<Clothes> clothesList = template.query(sql, param, CLOTHES_ROW_MAPPER);
		return clothesList;
	}

	/**
	 * 登録しているタグを検索します.
	 * 
	 * @param userId ログインユーザID
	 * @return タグ名の入ったリスト
	 */
	public List<TagContent> showTagName(Integer userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT ON (tc.name) tc.id,t.clothes_id,tc.name FROM clothes AS C ");
		sql.append("JOIN tags AS t ON c.id=t.clothes_id JOIN tag_contents AS tc ");
		sql.append("ON t.tag_contents_id=tc.id WHERE c.user_id=:userId AND c.deleted= false");
		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId);
		List<TagContent> tagNameList = template.query(sql.toString(), param, TAG_CONTENTS_ROW_MAPPER);
		return tagNameList;
	}

	/**
	 * 登録アイテムをタグ別に分けて表示します.
	 * 
	 * @param userId
	 * @param tagContentsId
	 * @return
	 */
	public List<Clothes> findByTag(Integer userId, Integer tagContentsId) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT c.id,c.user_id,c.category_id,c.brand_id,c.image_path,c.price,color_id,c.season,c.size_id,c.perchase_date,c.comment,c.deleted,");
		sql.append("t.id,t.clothes_id,tc.id,tc.name FROM clothes AS c ");
		sql.append("JOIN tags AS t ON c.id=t.clothes_id JOIN tag_contents AS tc ON t.tag_contents_id=tc.id ");
		sql.append("WHERE c.user_id=:userId AND tc.id=:tagContentsId AND deleted =false;");
		System.out.println(sql.toString());
		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId).addValue("tagContentsId",
				tagContentsId);
		List<Clothes> clothesList = template.query(sql.toString(), param, CLOTHES_ROW_MAPPER);
		return clothesList;
	}

	/**
	 * 統計画面用のローマッパー.
	 */
	private static final RowMapper<Clothes> CLOTHES_ROW_MAPPER2 = (rs, i) -> {
		Clothes clothes = new Clothes();
		clothes.setImagePath(rs.getString("cl_image_path"));
		clothes.setUserId(rs.getInt("cl_user_id"));
		clothes.setCategoryId(rs.getInt("cl_category_id"));
		clothes.setBrandId(rs.getInt("cl_brand_id"));
		clothes.setColorId(rs.getInt("cl_color_id"));
		clothes.setId(rs.getInt("cl_id"));
		clothes.setPrice(rs.getInt("price"));
		clothes.setSizeId(rs.getInt("cl_size_id"));
		clothes.setSeason(rs.getString("cl_season"));
		clothes.setPerchaseDate(rs.getDate("cl_perchase_date"));
		clothes.setComment(rs.getString("cl_comment"));
		clothes.setDeleted(rs.getBoolean("cl_deleted"));
		Category category = new Category();
		category.setId(rs.getInt("ca_id"));
		category.setName(rs.getString("ca_name"));
		clothes.setCategory(category);
		Brand brand = new Brand();
		brand.setId(rs.getInt("b_id"));
		brand.setName(rs.getString("b_name"));
		clothes.setBrand(brand);

		return clothes;
	};

	/**
	 * ユーザーIDで服データ検索.
	 * 
	 * @param userId ユーザーID
	 * @return clothesオブジェクトが入ったリスト（０件の場合nullを返します。）
	 */
	public List<Clothes> findByUserId(Integer userId) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT cl.id cl_id, cl.user_id cl_user_id, cl.image_path cl_image_path, CASE WHEN cl.price IS NULL THEN 0 ELSE cl.price END, ");
		sql.append(
				"cl.category_id cl_category_id, cl.brand_id cl_brand_id, cl.color_id cl_color_id, cl.size_id cl_size_id, ");
		sql.append(
				"cl.season cl_season, cl.perchase_date cl_perchase_date, cl.comment cl_comment, cl.deleted cl_deleted, ");
		sql.append(
				"ca.id ca_id, ca.name ca_name, b.id b_id, b.name b_name FROM clothes cl LEFT OUTER JOIN categories ca ON cl.category_id = ca.id ");
		sql.append(
				"LEFT OUTER JOIN brands b ON cl.brand_id = b.id WHERE user_id = :userId AND deleted = FALSE ORDER BY cl.id;");

		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId);
		List<Clothes> clothesList = template.query(sql.toString(), param, CLOTHES_ROW_MAPPER2);

		if (clothesList.size() == 0) {
			return null;
		}

		return clothesList;

	}

	/**
	 * 登録した商品とuserIdに結びつけてタグを登録する為に新規登録したclothesオブジェクトを取得.
	 * 
	 * @param userId
	 * @return
	 */
	public Clothes findNewClothes(Integer userId) {
		String sql = SQL + "FROM clothes WHERE 1=1 AND user_id=:userId order by id desc";
		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId);
		List<Clothes> clothesList = template.query(sql, param, CLOTHES_ROW_MAPPER);
		return clothesList.get(0);
	}

	/**
	 * ユーザーIDで服データ検索（カテゴリID昇順）.
	 * 
	 * @param userId ユーザーID
	 * @return clothesオブジェクトが入ったリスト（０件の場合nullを返します。）
	 */
	public List<Clothes> findByUserIdOrderByCategoryId(Integer userId) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT cl.id cl_id, cl.user_id cl_user_id, cl.image_path cl_image_path, CASE WHEN cl.price IS NULL THEN 0 ELSE cl.price END, ");
		sql.append(
				"cl.category_id cl_category_id, cl.brand_id cl_brand_id, cl.color_id cl_color_id, cl.size_id cl_size_id, ");
		sql.append(
				"cl.season cl_season, cl.perchase_date cl_perchase_date, cl.comment cl_comment, cl.deleted cl_deleted, ");
		sql.append(
				"ca.id ca_id, ca.name ca_name, b.id b_id, b.name b_name FROM clothes cl LEFT OUTER JOIN categories ca ON cl.category_id = ca.id ");
		sql.append(
				"LEFT OUTER JOIN brands b ON cl.brand_id = b.id WHERE user_id = :userId AND deleted = FALSE ORDER BY ca.id;");

		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId);
		List<Clothes> clothesList = template.query(sql.toString(), param, CLOTHES_ROW_MAPPER2);

		if (clothesList.size() == 0) {
			return null;
		}

		return clothesList;

	}

	/**
	 * ユーザーIDで服データ検索（ブランドID昇順）.
	 * 
	 * @param userId ユーザーID
	 * @return clothesオブジェクトが入ったリスト（０件の場合nullを返します。）
	 */
	public List<Clothes> findByUserIdOrderByBrandId(Integer userId) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT cl.id cl_id, cl.user_id cl_user_id, cl.image_path cl_image_path, CASE WHEN cl.price IS NULL THEN 0 ELSE cl.price END, ");
		sql.append(
				"cl.category_id cl_category_id, cl.brand_id cl_brand_id, cl.color_id cl_color_id, cl.size_id cl_size_id, ");
		sql.append(
				"cl.season cl_season, cl.perchase_date cl_perchase_date, cl.comment cl_comment, cl.deleted cl_deleted, ");
		sql.append(
				"ca.id ca_id, ca.name ca_name, b.id b_id, b.name b_name FROM clothes cl LEFT OUTER JOIN categories ca ON cl.category_id = ca.id ");
		sql.append(
				"LEFT OUTER JOIN brands b ON cl.brand_id = b.id WHERE user_id = :userId AND deleted = FALSE ORDER BY b.id;");

		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId);
		List<Clothes> clothesList = template.query(sql.toString(), param, CLOTHES_ROW_MAPPER2);

		if (clothesList.size() == 0) {
			return null;
		}

		return clothesList;

	}

	/**
	 * 服のカテゴリIDごとにリストに詰めて、それをまとめて大きなリストに詰めるResultSetExtractor
	 */
	private static final ResultSetExtractor<List<List<Clothes>>> CLOTHES_BY_CATEGORY_RESULT_SET_EXTRACTOR = (rs) -> {
		Clothes clothes = new Clothes();
		List<Clothes> clothesList = new ArrayList<>();
		List<List<Clothes>> bigClothesList = new ArrayList<>();

		int checkClothesId = 0;
		int checkCategoryId = 0;

		while (rs.next()) {
			int currentClothesId = rs.getInt("cl_id");
			if (currentClothesId != checkClothesId) {
				clothes = new Clothes();
				clothes.setId(rs.getInt("cl_id"));
				clothes.setUserId(rs.getInt("cl_user_id"));
				clothes.setCategoryId(rs.getInt("cl_category_id"));
				clothes.setBrandId(rs.getInt("cl_brand_id"));
				clothes.setColorId(rs.getInt("cl_color_id"));
				clothes.setImagePath(rs.getString("cl_image_path"));
				clothes.setPerchaseDate(rs.getDate("cl_perchase_date"));
				clothes.setSizeId(rs.getInt("cl_size_id"));
				clothes.setComment(rs.getString("cl_comment"));
				clothes.setDeleted(rs.getBoolean("cl_deleted"));
				clothes.setPrice(rs.getInt("price"));
				clothes.setSeason(rs.getString("cl_season"));
				Category category = new Category();
				category.setId(rs.getInt("ca_id"));
				category.setName(rs.getString("ca_name"));
			}

			if (rs.getInt("ca_id") != checkCategoryId) {
				clothesList = new ArrayList<>();
				clothesList.add(clothes);
				bigClothesList.add(clothesList);

			} else {
				clothesList.add(clothes);
			}

			checkClothesId = rs.getInt("cl_id");
			checkCategoryId = rs.getInt("ca_id");
		}

		System.out.println("最終的なリスト" + bigClothesList);
		System.out.println("biglistのサイズ" + bigClothesList.size());
		return bigClothesList;

	};

	/**
	 * ユーザーIDで服データ検索.(服のカテゴリごとにリスト化)
	 * 
	 * @param userId ユーザID
	 * @return
	 */
	public List<List<Clothes>> findByUserIdAndListedByCategoryId(Integer userId) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT cl.id cl_id, cl.user_id cl_user_id, cl.image_path cl_image_path, CASE WHEN cl.price IS NULL THEN 0 ELSE cl.price END, ");
		sql.append(
				"cl.category_id cl_category_id, cl.brand_id cl_brand_id, cl.color_id cl_color_id, cl.size_id cl_size_id, ");
		sql.append(
				"cl.season cl_season, cl.perchase_date cl_perchase_date, cl.comment cl_comment, cl.deleted cl_deleted, ");
		sql.append(
				"ca.id ca_id, ca.name ca_name FROM clothes cl LEFT OUTER JOIN categories ca ON cl.category_id = ca.id ");
		sql.append("WHERE user_id = :userId AND deleted = FALSE ORDER BY ca.id;");

		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId);
		List<List<Clothes>> bigClothesList = template.query(sql.toString(), param,
				CLOTHES_BY_CATEGORY_RESULT_SET_EXTRACTOR);

		if (bigClothesList.size() == 0) {
			return null;
		}

		return bigClothesList;

	}

	/**
	 * 服のブランドIDごとにリストに詰めて、それをまとめて大きなリストに詰めるResultSetExtractor
	 */
	private static final ResultSetExtractor<List<List<Clothes>>> CLOTHES_BY_BRAND_RESULT_SET_EXTRACTOR = (rs) -> {
		Clothes clothes = new Clothes();
		List<Clothes> clothesList = new ArrayList<>();
		List<List<Clothes>> bigClothesList = new ArrayList<>();

		int checkClothesId = 0;
		int checkBrandId = 0;

		while (rs.next()) {
			int currentClothesId = rs.getInt("cl_id");
			if (currentClothesId != checkClothesId) {
				clothes = new Clothes();
				clothes.setId(rs.getInt("cl_id"));
				clothes.setUserId(rs.getInt("cl_user_id"));
				clothes.setCategoryId(rs.getInt("cl_category_id"));
				clothes.setBrandId(rs.getInt("cl_brand_id"));
				clothes.setColorId(rs.getInt("cl_color_id"));
				clothes.setImagePath(rs.getString("cl_image_path"));
				clothes.setPerchaseDate(rs.getDate("cl_perchase_date"));
				clothes.setSizeId(rs.getInt("cl_size_id"));
				clothes.setComment(rs.getString("cl_comment"));
				clothes.setDeleted(rs.getBoolean("cl_deleted"));
				clothes.setPrice(rs.getInt("price"));
				clothes.setSeason(rs.getString("cl_season"));
				Brand brand = new Brand();
				brand.setId(rs.getInt("b_id"));
				brand.setName(rs.getString("b_name"));
			}

			if (rs.getInt("b_id") != checkBrandId) {
				clothesList = new ArrayList<>();
				clothesList.add(clothes);
				bigClothesList.add(clothesList);

			} else {
				clothesList.add(clothes);
			}

			checkClothesId = rs.getInt("cl_id");
			checkBrandId = rs.getInt("b_id");
		}

		System.out.println("最終的なリスト" + bigClothesList);
		System.out.println("biglistのサイズ" + bigClothesList.size());

		return bigClothesList;

	};

	/**
	 * ユーザーIDで服データ検索.(服のブランドごとにリスト化)
	 * 
	 * @param userId ユーザID
	 * @return
	 */
	public List<List<Clothes>> findByUserIdAndListedByBrandId(Integer userId) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT cl.id cl_id, cl.user_id cl_user_id, cl.image_path cl_image_path, CASE WHEN cl.price IS NULL THEN 0 ELSE cl.price END, ");
		sql.append(
				"cl.category_id cl_category_id, cl.brand_id cl_brand_id, cl.color_id cl_color_id, cl.size_id cl_size_id, ");
		sql.append(
				"cl.season cl_season, cl.perchase_date cl_perchase_date, cl.comment cl_comment, cl.deleted cl_deleted, ");
		sql.append("b.id b_id, b.name b_name FROM clothes cl LEFT OUTER JOIN brands b ON cl.brand_id = b.id ");
		sql.append("WHERE user_id = :userId AND deleted = FALSE ORDER BY b.id;");

		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId);
		List<List<Clothes>> bigClothesList = template.query(sql.toString(), param,
				CLOTHES_BY_BRAND_RESULT_SET_EXTRACTOR);

		if (bigClothesList.size() == 0) {
			return null;
		}

		return bigClothesList;
	}

	// アイテム詳細表示用のローマッパー.
	private static final RowMapper<Clothes> CLOTHES_ROW_MAPPER3 = (rs, i) -> {
		Clothes clothes = new Clothes();
		clothes.setId(rs.getInt("cl_id"));
		clothes.setUserId(rs.getInt("cl_user_id"));
		clothes.setBrandId(rs.getInt("cl_brand_id"));
		clothes.setCategoryId(rs.getInt("cl_category_id"));
		clothes.setColorId(rs.getInt("cl_color_id"));
		clothes.setSizeId(rs.getInt("cl_size_id"));
		clothes.setImagePath(rs.getString("cl_image_path"));
		clothes.setPrice(rs.getInt("cl_price"));
		clothes.setSeason(rs.getString("cl_season"));
		clothes.setPerchaseDate(rs.getDate("cl_perchase_date"));
		clothes.setComment(rs.getString("cl_comment"));
		// ブランド
		Brand brand = new Brand();
		brand.setId(rs.getInt("b_brand_id"));
		brand.setName(rs.getString("b_brand_name"));
		clothes.setBrand(brand);
		// カテゴリー
		Category category = new Category();
		category.setId(rs.getInt("ca_category_id"));
		category.setName(rs.getString("ca_category_name"));
		clothes.setCategory(category);
		// カラー
		Color color = new Color();
		color.setId(rs.getInt("co_id"));
		color.setName(rs.getString("co_color_name"));
		clothes.setColor(color);
		// サイズ
		Size size = new Size();
		size.setId(rs.getInt("s_id"));
		size.setName(rs.getString("s_size_name"));
		clothes.setSize(size);

		return clothes;
	};

	/**
	 * アイテムIDで1件検索を行います.
	 * 
	 * @param id アイテムID
	 * @return 1件のアイテム情報
	 */
	public Clothes findById(Integer id) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT cl.id cl_id,cl.user_id cl_user_id,ca.name ca_name,b.id b_brand_id,b.name b_brand_name,ca.id ca_category_id,ca.name ca_category_name,"
						+ "cl.image_path cl_image_path,cl.price cl_price,co.id co_id,co.name co_color_name,cl.season cl_season,"
						+ "s.id s_id,s.name s_size_name,cl.perchase_date cl_perchase_date,cl.comment cl_comment,cl.brand_id cl_brand_id,"
						+ "cl.category_id cl_category_id,cl.color_id cl_color_id,cl.size_id cl_size_id ");
		sql.append("FROM clothes cl ");
		sql.append("LEFT JOIN categories ca ON cl.category_id = ca.id ");
		sql.append("LEFT JOIN brands b ON cl.brand_id=b.id ");
		sql.append("LEFT JOIN colors co ON cl.color_id=co.id ");
		sql.append("LEFT JOIN sizes s ON cl.size_id=s.id ");
		sql.append("WHERE cl.id=:id");

		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
		Clothes clothes = template.queryForObject(sql.toString(), param, CLOTHES_ROW_MAPPER3);

		return clothes;
	}

	/**
	 * アイテムの更新を行うメソッドです.
	 * 
	 * @param clothes clothesオブジェクト
	 */
	public void update(Clothes clothes) {
		SqlParameterSource param = new BeanPropertySqlParameterSource(clothes);

		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE clothes ");
		sql.append(
				"SET category_id=:categoryId, brand_id=:brandId, color_id=:colorId, season=:season, size_id=:sizeId,");
		sql.append("price=:price, perchase_date=:perchaseDate, comment=:comment ");
		sql.append("WHERE id=:id ");
		sql.append("AND user_id=:userId;");

		template.update(sql.toString(), param);
	}

	/**
	 * アイテムの削除を行う.
	 * 
	 * @param id
	 */
	public void deleteClothes(Integer id) {
		String sql = "UPDATE clothes SET deleted=true WHERE id=:id";
		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
		template.update(sql, param);
	}
		
	/**
	 * 月毎の購入金額合計、アイテム数、平均金額を検索します.
	 * 
	 * @param userId       ユーザID
	 * @param perchaseDate 購入日
	 * @return データの入ったリスト
	 */
	public List<MonthlyDataDTO> showByPerchaseData(Integer userId, String perchaseDate) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT TO_CHAR(perchase_date,'YYYY-MM') as month,");
		sql.append("SUM(price) AS total_price ,COUNT(perchase_date) AS item_quantity,TRUNC(AVG(price),0) AS price_average ");
		sql.append("FROM clothes where user_id=:userId AND deleted='false' ");
		sql.append("GROUP BY month HAVING TO_CHAR(perchase_date,'YYYY-MM')=:perchaseDate ");
		sql.append("ORDER BY month;");

		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId).addValue("perchaseDate",
				perchaseDate);
		List<MonthlyDataDTO> clothesPriceList = template.query(sql.toString(), param, DATA_ROW_MAPPER);

		return clothesPriceList;

	}

}
