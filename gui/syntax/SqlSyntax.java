package gui.syntax;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class SqlSyntax implements Syntax {
	private Map<Pattern, String> syntax = new LinkedHashMap<Pattern, String>();

	public SqlSyntax() {
		addKeyword("select", "statement");
		addKeyword("insert", "statement");
		addKeyword("update", "statement");
		addKeyword("delete", "statement");
		addKeyword("alter", "statement");
		addKeyword("create", "statement");
		addKeyword("drop", "statement");
		addKeyword("declare", "statement");
		addKeyword("transaction", "statement");
		addKeyword("table", "statement");
		addKeyword("procedure", "statement");
		addKeyword("view", "statement");
		addKeyword("commit", "statement");
		addKeyword("rollback", "statement");
		addKeyword("execute", "statement");

		addKeyword("top", "keyword");
		addKeyword("from", "keyword");
		addKeyword("on", "keyword");
		addKeyword("where", "keyword");
		addKeyword("group", "keyword");
		addKeyword("by", "keyword");
		addKeyword("having", "keyword");
		addKeyword("order", "keyword");
		addKeyword("asc", "keyword");
		addKeyword("desc", "keyword");
		addKeyword("as", "keyword");
		addKeyword("case", "keyword");
		addKeyword("when", "keyword");
		addKeyword("then", "keyword");
		addKeyword("else", "keyword");
		addKeyword("end", "keyword");
		addKeyword("inner", "keyword");
		addKeyword("outer", "keyword");
		addKeyword("cross", "keyword");
		addKeyword("right", "keyword");
		addKeyword("left", "keyword");
		addKeyword("join", "keyword");

		addKeyword("sum", "function");
		addKeyword("count", "function");
		addKeyword("average", "function");
		addKeyword("max", "function");
		addKeyword("min", "function");
		addKeyword("iif", "function");
		addKeyword("now", "function");
		addKeyword("getdate", "function");
		addKeyword("convert", "function");
		addKeyword("format", "function");

		addKeyword("in", "operator");
		addKeyword("or", "operator");
		addKeyword("and", "operator");
		addKeyword("between", "operator");
		addKeyword("like", "operator");
		addKeyword("union", "operator");
		addKeyword("intersect", "operator");
		addKeyword("any", "operator");
		addKeyword("some", "operator");
		addKeyword("all", "operator");
		addKeyword("minus", "operator");
		addKeyword("distinct", "operator");

		addKeyword("integer", "type");
		addKeyword("smallint", "type");
		addKeyword("int", "type");
		addKeyword("char", "type");
		addKeyword("long", "type");
		addKeyword("varchar", "type");
		addKeyword("float", "type");
		addKeyword("double", "type");
		addKeyword("numeric", "type");
		addKeyword("real", "type");
		addKeyword("bit", "type");
		addKeyword("boolean", "type");
		addKeyword("money", "type");
		addKeyword("date", "type");
		addKeyword("datetime", "type");
		addKeyword("smalldate", "type");
		addKeyword("time", "type");
		addKeyword("timestamp", "type");

		syntax.put(Pattern.compile("@\\w+\\b"), "variable");
		syntax.put(Pattern.compile("^@\\w+\\b"), "variable");

		syntax.put(Pattern.compile("\\b\\d++\\b"), "number");
		syntax.put(Pattern.compile("\\b\\d++\\.\\d++\\b"), "number");
		syntax.put(Pattern.compile("\\.\\d++\\b"), "number");
		syntax.put(Pattern.compile("\\b\\d++\\."), "number");

		syntax.put(Pattern.compile("'(\\\\'|[^'])*'"), "string");
		syntax.put(Pattern.compile("\"(\\\\\"|[^\"])*\""), "string");

		// FIXME:
		// Overlapping comment groups can cause a problem
		syntax.put(Pattern.compile("--.*$"), "comment");
		syntax.put(Pattern.compile("(?s)/\\*.*?\\*/"), "comment");
	}

	private void addKeyword(String keyword, String type) {
		syntax.put(Pattern.compile("(?i)\\b" + keyword + "\\b"), type);
	}

	public Map<Pattern, String> getSyntax() {
		return syntax;
	}
}

