/**
 * Implements the Syntax interface with
 * common SQL keywords.
 */

package cvosteen.sqltool.gui.syntax;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class SqlSyntax implements Syntax {
	private Map<Pattern, String> syntax = new LinkedHashMap<Pattern, String>();
	private String[] statements = new String[] {
		"alter",
		"analyze",
		"attach",
		"backup",
		"begin",
		"break",
		"cascade",
		"catch",
		"commit",
		"compute",
		"create",
		"declare",
		"delete",
		"deny",
		"detach",
		"drop",
		"explain",
		"execute",
		"fail",
		"fetch",
		"finally",
		"go",
		"goto",
		"grant",
		"insert",
		"kill",
		"merge",
		"open",
		"output",
		"print",
		"procedure",
		"raise",
		"receive",
		"release",
		"reindex",
		"replace",
		"restore",
		"revert",
		"revoke",
		"rollback",
		"select",
		"set",
		"shutdown",
		"try",
		"update",
		"waitfor",
	};

	private String[] keywords = new String[] {
		"abort",
		"action",
		"after",
		"as",
		"asc",
		"autoincrement",
		"before",
		"by",
		"ceiling",
		"check",
		"collate",
		"column",
		"conflict",
		"constraint",
		"cross",
		"cursor",
		"database",
		"default",
		"deferrable",
		"deferred",
		"desc",
		"end",
		"escape",
		"exclusive",
		"floor",
		"for",
		"foreign",
		"from",
		"full",
		"group",
		"grouping",
		"having",
		"on",
		"identity",
		"ignore",
		"immediate",
		"index",
		"indexed",
		"initially",
		"inner",
		"instead",
		"into",
		"join",
		"key",
		"left",
		"limit",
		"match",
		"natural",
		"no",
		"null",
		"of",
		"offset",
		"on",
		"option",
		"order",
		"outer",
		"over",
		"plan",
		"pragma",
		"primary",
		"query",
		"rank",
		"references",
		"regexp",
		"rename",
		"restrict",
		"right",
		"row",
		"savepoint",
		"soundex",
		"table",
		"temp",
		"temporary",
		"to",
		"top",
		"transaction",
		"trigger",
		"use",
		"using",
		"vacuum",
		"values",
		"view",
		"virtual",
		"where",
		"while"
	};

	private String[] operators = new String[] {
		"add",
		"all",
		"and",
		"any",
		"between",
		"case",
		"cast",
		"close",
		"contains",
		"distinct",
		"each",
		"else",
		"except",
		"exists",
		"when",
		"if",
		"in",
		"intersect",
		"is",
		"isdate",
		"isnull",
		"isnumeric",
		"like",
		"minus",
		"not",
		"notnull",
		"or",
		"some",
		"then",
		"union",
		"unique",
		"when"
	};

	private String[] functions = new String[] {
		"abs",
		"average",
		"avg",
		"changes",
		"charindex",
		"coalesce",
		"convert",
		"count",
		"current_date",
		"current_time",
		"current_timestamp",
		"day",
		"format",
		"getdate",
		"ifnull",
		"iif",
		"hex",
		"len",
		"length",
		"lower",
		"ltrim",
		"max",
		"mid",
		"min",
		"month",
		"now",
		"nullif",
		"quote",
		"random",
		"replace",
		"reverse",
		"round",
		"rtrim",
		"str",
		"substring",
		"sum",
		"total",
		"trim",
		"upper",
		"val",
		"year"
	};

	private String[] types = new String[] {
		"bigint",
		"binary",
		"bit",
		"blob",
		"boolean",
		"char",
		"character",
		"clob",
		"date",
		"datetime",
		"decimal",
		"double",
		"float",
		"glob",
		"int",
		"int2",
		"int8",
		"integer",
		"long",
		"mediumint",
		"money",
		"nchar",
		"numeric",
		"nvarchar",
		"real",
		"smalldate",
		"smallint",
		"text",
		"time",
		"timestamp",
		"tinyint",
		"unsigned",
		"varchar"
	};

	public SqlSyntax() {
		for(String statement : statements)
			addKeyword(statement, "statement");

		for(String keyword : keywords)
			addKeyword(keyword, "keyword");

		for(String operator : operators)
			addKeyword(operator, "operator");

		for(String function : functions)
			addKeyword(function, "function");

		for(String type: types)
			addKeyword(type, "type");

		syntax.put(Pattern.compile("@\\w+\\b"), "variable");
		syntax.put(Pattern.compile("^@\\w+\\b"), "variable");

		syntax.put(Pattern.compile("\\b\\d++\\b"), "number");
		syntax.put(Pattern.compile("\\b\\d++\\.\\d++\\b"), "number");
		syntax.put(Pattern.compile("\\.\\d++\\b"), "number");
		syntax.put(Pattern.compile("\\b\\d++\\."), "number");

		syntax.put(Pattern.compile("'(\\\\'|[^'])*'"), "string");
		syntax.put(Pattern.compile("\"(\\\\\"|[^\"])*\""), "string");

		// FIXME:
		// Overlapping comment groups can cause a problem.
		// However, this should work well for most cases.
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

