package com.codequest.learn.data

import com.codequest.learn.model.*

/**
 * Curated offline question bank. These serve as:
 * 1. Immediate content on first launch (before any AI generation)
 * 2. Fallback when offline or without an API key
 * 3. Baseline quality standard for the app
 */
object SeedQuestions {

    val all: List<ChallengeQuestion> by lazy { pythonQuestions + javaQuestions }

    // ═══════════════════════════════════════════
    //  PYTHON
    // ═══════════════════════════════════════════

    private val pythonQuestions = listOf(
        // ── Daily Quiz ──
        ChallengeQuestion(
            id = "seed_py_quiz_b1",
            language = ProgrammingLanguage.PYTHON,
            tier = SkillTier.BEGINNER,
            mode = ChallengeMode.DAILY_QUIZ,
            title = "List Slicing Behavior",
            prompt = "What does nums[1:4] return?",
            codeSnippet = "nums = [10, 20, 30, 40, 50]\nsub = nums[1:4]",
            options = listOf(
                "[20, 30, 40]",
                "[20, 30, 40, 50]",
                "[10, 20, 30, 40]",
                "IndexError"
            ),
            correctIndex = 0,
            hint = "Slice syntax start:stop includes start but excludes stop.",
            explanation = "nums[1:4] returns elements at indices 1, 2, 3 → [20, 30, 40]. The stop index is exclusive."
        ),
        ChallengeQuestion(
            id = "seed_py_quiz_b2",
            language = ProgrammingLanguage.PYTHON,
            tier = SkillTier.BEGINNER,
            mode = ChallengeMode.DAILY_QUIZ,
            title = "Type of Division Result",
            prompt = "What is the type of the result?",
            codeSnippet = "result = 10 / 3",
            options = listOf(
                "float",
                "int",
                "str",
                "Depends on the operands"
            ),
            correctIndex = 0,
            hint = "The / operator in Python 3 always performs true division.",
            explanation = "In Python 3, the / operator always returns a float (3.333...), even when both operands are integers. Use // for integer division."
        ),
        ChallengeQuestion(
            id = "seed_py_quiz_i1",
            language = ProgrammingLanguage.PYTHON,
            tier = SkillTier.INTERMEDIATE,
            mode = ChallengeMode.DAILY_QUIZ,
            title = "Dictionary .get() Safety",
            prompt = "How do you safely get key 'score' from dict player, defaulting to 0?",
            codeSnippet = "player = {'name': 'Aarav', 'level': 4}",
            options = listOf(
                "player.get('score', 0)",
                "player['score', 0]",
                "player.fetch('score') or 0",
                "player.find('score', default=0)"
            ),
            correctIndex = 0,
            hint = "Dictionaries have a built-in two-argument method that avoids KeyError.",
            explanation = "dict.get(key, default) returns the value if the key exists, otherwise returns the fallback value without raising KeyError."
        ),
        ChallengeQuestion(
            id = "seed_py_quiz_i2",
            language = ProgrammingLanguage.PYTHON,
            tier = SkillTier.INTERMEDIATE,
            mode = ChallengeMode.DAILY_QUIZ,
            title = "Tuple Unpacking",
            prompt = "What values do a, b, c hold after this unpacking?",
            codeSnippet = "data = (100, 200, 300)\na, b, c = data",
            options = listOf(
                "a=100, b=200, c=300",
                "a=(100,), b=(200,), c=(300,)",
                "ValueError: too many values",
                "a=data, b=data, c=data"
            ),
            correctIndex = 0,
            hint = "Python can unpack iterables into individual variables when the count matches.",
            explanation = "Tuple unpacking assigns each element to the corresponding variable. Since data has 3 elements and there are 3 targets, it works perfectly."
        ),
        ChallengeQuestion(
            id = "seed_py_quiz_e1",
            language = ProgrammingLanguage.PYTHON,
            tier = SkillTier.EXPERT,
            mode = ChallengeMode.DAILY_QUIZ,
            title = "Closure Variable Binding",
            prompt = "What does funcs[0]() return?",
            codeSnippet = "funcs = []\nfor i in range(3):\n    funcs.append(lambda: i)\n\nprint(funcs[0]())",
            options = listOf("2", "0", "1", "None"),
            correctIndex = 0,
            hint = "Lambda captures the variable reference, not the value at creation time.",
            explanation = "All three lambdas capture the same variable i. By the time any is called, the loop has finished and i = 2, so every lambda returns 2."
        ),

        // ── Missing Words ──
        ChallengeQuestion(
            id = "seed_py_miss_b1",
            language = ProgrammingLanguage.PYTHON,
            tier = SkillTier.BEGINNER,
            mode = ChallengeMode.MISSING_WORDS,
            title = "Define a Filter Function",
            prompt = "Fill in the missing keywords to define a function that collects even numbers.",
            codeSnippet = "___1___ get_evens(numbers):\n    result = []\n    ___2___ n in numbers:\n        if n % 2 == 0:\n            result.___3___(n)\n    return result",
            missingBlanks = listOf("def", "for", "append"),
            wordBank = listOf("def", "for", "append", "func", "push", "while"),
            hint = "Python uses 'def' for functions and 'append' for adding to lists.",
            explanation = "Use def to declare a function, for to iterate, and .append(n) to add each even item."
        ),
        ChallengeQuestion(
            id = "seed_py_miss_i1",
            language = ProgrammingLanguage.PYTHON,
            tier = SkillTier.INTERMEDIATE,
            mode = ChallengeMode.MISSING_WORDS,
            title = "List Comprehension with Filter",
            prompt = "Complete this list comprehension that squares only positive values.",
            codeSnippet = "values = [-3, 2, -1, 5]\nsquares = [x ** 2 ___1___ x ___2___ values ___3___ x > 0]",
            missingBlanks = listOf("for", "in", "if"),
            wordBank = listOf("for", "in", "if", "where", "from", "when"),
            hint = "Comprehension syntax: [expr for item in iterable if condition]",
            explanation = "List comprehensions combine iteration (for x in values) and filtering (if x > 0) in one expression."
        ),
        ChallengeQuestion(
            id = "seed_py_miss_e1",
            language = ProgrammingLanguage.PYTHON,
            tier = SkillTier.EXPERT,
            mode = ChallengeMode.MISSING_WORDS,
            title = "Context Manager Protocol",
            prompt = "Complete the context manager class for a file handler.",
            codeSnippet = "class FileHandler:\n    def ___1___(self, name):\n        self.file = open(name)\n        return self.file\n    def ___2___(self, *args):\n        self.file.___3___()",
            missingBlanks = listOf("__enter__", "__exit__", "close"),
            wordBank = listOf("__enter__", "__exit__", "close", "__init__", "__del__", "flush"),
            hint = "Context managers implement two dunder methods for entering and exiting the with block.",
            explanation = "__enter__ is called on 'with' entry and returns the resource. __exit__ is called on exit and should clean up (close the file)."
        ),

        // ── Guess Output ──
        ChallengeQuestion(
            id = "seed_py_out_i1",
            language = ProgrammingLanguage.PYTHON,
            tier = SkillTier.INTERMEDIATE,
            mode = ChallengeMode.GUESS_OUTPUT,
            title = "Mutable Default Argument",
            prompt = "What is printed after calling add_item twice?",
            codeSnippet = "def add_item(val, box=[]):\n    box.append(val)\n    return box\n\nadd_item(1)\nprint(add_item(2))",
            options = listOf("[1, 2]", "[2]", "[[1], [2]]", "TypeError"),
            correctIndex = 0,
            hint = "Default parameters are evaluated once when def executes, not per call.",
            explanation = "The default list box=[] is created once. Both calls share the same list, so it accumulates: [1, 2]."
        ),
        ChallengeQuestion(
            id = "seed_py_out_b1",
            language = ProgrammingLanguage.PYTHON,
            tier = SkillTier.BEGINNER,
            mode = ChallengeMode.GUESS_OUTPUT,
            title = "String Repetition",
            prompt = "What does this code print?",
            codeSnippet = "word = 'ha'\nprint(word * 3)",
            options = listOf("hahaha", "ha ha ha", "ha*3", "Error"),
            correctIndex = 0,
            hint = "The * operator on strings repeats them.",
            explanation = "The * operator repeats a string N times. 'ha' * 3 concatenates 'ha' three times: 'hahaha'."
        ),
        ChallengeQuestion(
            id = "seed_py_out_e1",
            language = ProgrammingLanguage.PYTHON,
            tier = SkillTier.EXPERT,
            mode = ChallengeMode.GUESS_OUTPUT,
            title = "Generator Lazy Evaluation",
            prompt = "What does this snippet output?",
            codeSnippet = "nums = [1, 2, 3]\ngen = (x * factor for x in nums)\nfactor = 10\nprint(list(gen))",
            options = listOf("[10, 20, 30]", "[1, 2, 3]", "NameError", "[0, 0, 0]"),
            correctIndex = 0,
            hint = "Generator expressions evaluate lazily at iteration time, not at creation time.",
            explanation = "The generator looks up 'factor' when iterated via list(gen), by which time factor = 10."
        ),

        // ── Logic Builder ──
        ChallengeQuestion(
            id = "seed_py_logic_e1",
            language = ProgrammingLanguage.PYTHON,
            tier = SkillTier.EXPERT,
            mode = ChallengeMode.LOGIC_BUILDER,
            title = "Binary Search Algorithm",
            prompt = "Arrange these code blocks to implement iterative binary search.",
            codeSnippet = "# Find target in sorted list arr, return index or -1",
            logicStepsCorrectOrder = listOf(
                "low, high = 0, len(arr) - 1",
                "while low <= high:",
                "    mid = (low + high) // 2",
                "    if arr[mid] == target: return mid",
                "    elif arr[mid] < target: low = mid + 1",
                "    else: high = mid - 1",
                "return -1"
            ),
            hint = "Initialize boundaries, loop while valid range, compute mid, compare, adjust boundaries, return -1 if not found.",
            explanation = "Binary search halves the range each iteration by comparing arr[mid] with target, achieving O(log n) time."
        ),
        ChallengeQuestion(
            id = "seed_py_logic_i1",
            language = ProgrammingLanguage.PYTHON,
            tier = SkillTier.INTERMEDIATE,
            mode = ChallengeMode.LOGIC_BUILDER,
            title = "Bubble Sort Steps",
            prompt = "Arrange these lines to implement a basic bubble sort.",
            codeSnippet = "# Sort list arr in ascending order",
            logicStepsCorrectOrder = listOf(
                "n = len(arr)",
                "for i in range(n):",
                "    for j in range(0, n - i - 1):",
                "        if arr[j] > arr[j + 1]:",
                "            arr[j], arr[j + 1] = arr[j + 1], arr[j]"
            ),
            hint = "Get length, outer loop for passes, inner loop for comparisons, swap if out of order.",
            explanation = "Bubble sort repeatedly steps through the list, compares adjacent elements, and swaps them if needed. Each pass bubbles the largest unsorted element to the end."
        )
    )

    // ═══════════════════════════════════════════
    //  JAVA
    // ═══════════════════════════════════════════

    private val javaQuestions = listOf(
        // ── Daily Quiz ──
        ChallengeQuestion(
            id = "seed_jv_quiz_b1",
            language = ProgrammingLanguage.JAVA,
            tier = SkillTier.BEGINNER,
            mode = ChallengeMode.DAILY_QUIZ,
            title = "String Comparison in Java",
            prompt = "Why use .equals() instead of == for String comparison?",
            codeSnippet = "String a = new String(\"Code\");\nString b = new String(\"Code\");",
            options = listOf(
                "== checks reference identity, .equals() checks value equality",
                "== is a syntax error for Objects",
                ".equals() ignores case automatically",
                "== only works in static methods"
            ),
            correctIndex = 0,
            hint = "Think about heap object references vs. character sequence content.",
            explanation = "In Java, == on references checks if both point to the same memory address. .equals() checks if the contents are identical."
        ),
        ChallengeQuestion(
            id = "seed_jv_quiz_b2",
            language = ProgrammingLanguage.JAVA,
            tier = SkillTier.BEGINNER,
            mode = ChallengeMode.DAILY_QUIZ,
            title = "Array Declaration Syntax",
            prompt = "Which is the correct way to declare and initialize an int array?",
            codeSnippet = "// Choose the valid declaration:",
            options = listOf(
                "int[] nums = {1, 2, 3};",
                "int nums[] = new [3]{1, 2, 3};",
                "Array<int> nums = [1, 2, 3];",
                "int nums = new int(3);"
            ),
            correctIndex = 0,
            hint = "Java arrays use curly braces for inline initialization.",
            explanation = "int[] nums = {1, 2, 3}; is the shorthand array initialization syntax. The type is int[], not Array<int>."
        ),
        ChallengeQuestion(
            id = "seed_jv_quiz_i1",
            language = ProgrammingLanguage.JAVA,
            tier = SkillTier.INTERMEDIATE,
            mode = ChallengeMode.DAILY_QUIZ,
            title = "ArrayList vs Array",
            prompt = "What is a key difference between ArrayList and a plain array in Java?",
            codeSnippet = "int[] arr = new int[5];\nArrayList<Integer> list = new ArrayList<>();",
            options = listOf(
                "ArrayList can grow dynamically; arrays have a fixed size",
                "Arrays are faster because they use generics",
                "ArrayList can hold primitives directly",
                "There is no practical difference"
            ),
            correctIndex = 0,
            hint = "Consider what happens when you need to add more elements than the initial capacity.",
            explanation = "Arrays have a fixed size set at creation. ArrayList automatically resizes as elements are added. Note: ArrayList requires wrapper types (Integer, not int)."
        ),
        ChallengeQuestion(
            id = "seed_jv_quiz_e1",
            language = ProgrammingLanguage.JAVA,
            tier = SkillTier.EXPERT,
            mode = ChallengeMode.DAILY_QUIZ,
            title = "Interface vs Abstract Class",
            prompt = "When should you prefer an interface over an abstract class in Java?",
            codeSnippet = "// Design decision for a contract",
            options = listOf(
                "When you need multiple inheritance of type and a class can implement multiple interfaces",
                "When you need to store instance state that subclasses inherit",
                "When you want to prevent any class from instantiating",
                "Interfaces are always faster than abstract classes"
            ),
            correctIndex = 0,
            hint = "Java allows a class to implement multiple _____ but extend only one _____.",
            explanation = "A class can implement multiple interfaces but extend only one class. Interfaces define contracts without forcing a single inheritance chain."
        ),

        // ── Missing Words ──
        ChallengeQuestion(
            id = "seed_jv_miss_b1",
            language = ProgrammingLanguage.JAVA,
            tier = SkillTier.BEGINNER,
            mode = ChallengeMode.MISSING_WORDS,
            title = "Java Main Method & Loop",
            prompt = "Fill in the missing keywords to complete the main method and loop.",
            codeSnippet = "public class Main {\n    public ___1___ void main(String[] args) {\n        ___2___ (int i = 0; i < 3; i++) {\n            System.out.___3___(i);\n        }\n    }\n}",
            missingBlanks = listOf("static", "for", "println"),
            wordBank = listOf("static", "for", "println", "void", "foreach", "echo"),
            hint = "The JVM entry point needs a special modifier before void.",
            explanation = "public static void main is the JVM entry point. for runs the counter loop. System.out.println prints each value."
        ),
        ChallengeQuestion(
            id = "seed_jv_miss_i1",
            language = ProgrammingLanguage.JAVA,
            tier = SkillTier.INTERMEDIATE,
            mode = ChallengeMode.MISSING_WORDS,
            title = "Try-Catch Exception Handling",
            prompt = "Complete the exception handling block.",
            codeSnippet = "___1___ {\n    int result = 10 / 0;\n} ___2___ (ArithmeticException e) {\n    System.out.println(e.___3___());\n}",
            missingBlanks = listOf("try", "catch", "getMessage"),
            wordBank = listOf("try", "catch", "getMessage", "attempt", "handle", "toString"),
            hint = "Java exception handling uses two paired keywords for guarded code and error handling.",
            explanation = "try wraps risky code. catch handles specific exceptions. getMessage() returns the error description."
        ),
        ChallengeQuestion(
            id = "seed_jv_miss_e1",
            language = ProgrammingLanguage.JAVA,
            tier = SkillTier.EXPERT,
            mode = ChallengeMode.MISSING_WORDS,
            title = "Generic Method Signature",
            prompt = "Complete this generic method that returns the first element of any list.",
            codeSnippet = "public ___1___ <T> T getFirst(___2___<T> items) {\n    return items.___3___(0);\n}",
            missingBlanks = listOf("static", "List", "get"),
            wordBank = listOf("static", "List", "get", "final", "Array", "fetch"),
            hint = "Generic methods declare type parameters in angle brackets before the return type.",
            explanation = "static makes it callable without an instance. <T> declares the type parameter. List<T> is the parameterized collection. get(0) retrieves the first element."
        ),

        // ── Guess Output ──
        ChallengeQuestion(
            id = "seed_jv_out_b1",
            language = ProgrammingLanguage.JAVA,
            tier = SkillTier.BEGINNER,
            mode = ChallengeMode.GUESS_OUTPUT,
            title = "Integer Division & Concatenation",
            prompt = "What does this print?",
            codeSnippet = "int a = 7;\nint b = 2;\nSystem.out.println(a / b + \"_\" + a % b);",
            options = listOf("3_1", "3.5_1", "31", "3.5_0.5"),
            correctIndex = 0,
            hint = "Both operands are int, so division truncates the decimal.",
            explanation = "Integer division 7/2 = 3 (truncated). Remainder 7%2 = 1. Concatenated with \"_\" gives \"3_1\"."
        ),
        ChallengeQuestion(
            id = "seed_jv_out_i1",
            language = ProgrammingLanguage.JAVA,
            tier = SkillTier.INTERMEDIATE,
            mode = ChallengeMode.GUESS_OUTPUT,
            title = "String Concatenation Order",
            prompt = "What is printed?",
            codeSnippet = "System.out.println(1 + 2 + \" apples \" + 3 + 4);",
            options = listOf("3 apples 34", "12 apples 34", "3 apples 7", "12 apples 7"),
            correctIndex = 0,
            hint = "Java evaluates left to right. Once a String appears, + becomes concatenation.",
            explanation = "1+2 = 3 (arithmetic). Then 3 + \" apples \" = \"3 apples \". Then string + 3 = \"3 apples 3\", + 4 = \"3 apples 34\"."
        ),
        ChallengeQuestion(
            id = "seed_jv_out_e1",
            language = ProgrammingLanguage.JAVA,
            tier = SkillTier.EXPERT,
            mode = ChallengeMode.GUESS_OUTPUT,
            title = "Autoboxing & Integer Cache",
            prompt = "What is printed?",
            codeSnippet = "Integer x = 127;\nInteger y = 127;\nInteger a = 128;\nInteger b = 128;\nSystem.out.println(x == y);\nSystem.out.println(a == b);",
            options = listOf("true\nfalse", "true\ntrue", "false\nfalse", "false\ntrue"),
            correctIndex = 0,
            hint = "Java caches Integer objects for values in a specific range.",
            explanation = "Java caches Integer values from -128 to 127. For 127, x and y reference the same cached object (== is true). For 128, new objects are created (== is false)."
        ),

        // ── Logic Builder ──
        ChallengeQuestion(
            id = "seed_jv_logic_e1",
            language = ProgrammingLanguage.JAVA,
            tier = SkillTier.EXPERT,
            mode = ChallengeMode.LOGIC_BUILDER,
            title = "Two-Sum HashMap Solution",
            prompt = "Order these statements to solve Two-Sum in O(n) using a HashMap.",
            codeSnippet = "// Return indices whose elements sum to target",
            logicStepsCorrectOrder = listOf(
                "Map<Integer, Integer> seen = new HashMap<>();",
                "for (int i = 0; i < nums.length; i++) {",
                "    int complement = target - nums[i];",
                "    if (seen.containsKey(complement)) {",
                "        return new int[]{seen.get(complement), i};",
                "    }",
                "    seen.put(nums[i], i);",
                "}"
            ),
            hint = "Create map first, loop through array, compute complement, check map, store current.",
            explanation = "By storing each number and its index in a HashMap, we check for the complement in O(1), achieving O(n) total time."
        ),
        ChallengeQuestion(
            id = "seed_jv_logic_i1",
            language = ProgrammingLanguage.JAVA,
            tier = SkillTier.INTERMEDIATE,
            mode = ChallengeMode.LOGIC_BUILDER,
            title = "Reverse a String",
            prompt = "Arrange the code to reverse a string using StringBuilder.",
            codeSnippet = "// Reverse the input string s",
            logicStepsCorrectOrder = listOf(
                "StringBuilder sb = new StringBuilder(s);",
                "sb.reverse();",
                "return sb.toString();"
            ),
            hint = "StringBuilder has a built-in reverse method.",
            explanation = "Create a StringBuilder from the string, call reverse() to reverse in place, then convert back to String with toString()."
        )
    )

    val sampleLeaderboard: List<LeaderboardEntry> = listOf(
        LeaderboardEntry(1, "Aarav Patel", "Python + Java", 22, 1420),
        LeaderboardEntry(2, "Meera Sharma", "Java", 18, 1180),
        LeaderboardEntry(3, "Riya Nair", "Python", 14, 960),
        LeaderboardEntry(4, "Dev Gupta", "Python + Java", 9, 720),
        LeaderboardEntry(5, "Ananya Roy", "Java", 6, 510),
        LeaderboardEntry(6, "Kabir Singh", "Python", 3, 290)
    )
}
