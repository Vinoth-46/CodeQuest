package com.codequest.learn.data

import com.codequest.learn.model.*

object LearningCatalog {

    val paths: List<LearningPath> = listOf(
        LearningPath(
            id = "web-dev",
            name = "Web Dev",
            description = "Modern Fullstack JavaScript, React, Next.js & Serverless Systems",
            milestones = listOf(
                PathMilestone("wd-1", "Beginner", "HTML5, CSS3 & Modern JavaScript", "Semantic layouts, Flexbox/Grid, and ES6+ DOM manipulation.", 16, isCompleted = true, isUnlocked = true),
                PathMilestone("wd-2", "Intermediate", "React, State & Components", "Hooks, state lifecycles, props drilling & custom event flows.", 22, isCompleted = false, isUnlocked = true),
                PathMilestone("wd-3", "Advanced", "Fullstack Next.js & SSR", "Server components, streaming mutations & route handlers.", 28, isCompleted = false, isUnlocked = false),
                PathMilestone("wd-4", "Expert", "Micro-frontends & Scale", "Edge caching, CDN strategies & zero-bundle microservices.", 18, isCompleted = false, isUnlocked = false)
            )
        ),
        LearningPath(
            id = "python",
            name = "Python",
            description = "From syntax fundamentals to async frameworks & backend architecture",
            milestones = listOf(
                PathMilestone("py-1", "Beginner", "Syntax, Loops & Types", "Variables, lists, dicts, conditionals and core data structures.", 14, isCompleted = true, isUnlocked = true),
                PathMilestone("py-2", "Intermediate", "OOP, Dunder Methods & Files", "Classes, inheritance, context managers and robust exception handling.", 20, isCompleted = false, isUnlocked = true),
                PathMilestone("py-3", "Advanced", "Asyncio & FastAPI Services", "Event loops, coroutines, Pydantic schemas and dependency injection.", 26, isCompleted = false, isUnlocked = false),
                PathMilestone("py-4", "Expert", "Distributed Python & GIL", "Multiprocessing, Cython bindings and high-throughput pipelines.", 15, isCompleted = false, isUnlocked = false)
            )
        ),
        LearningPath(
            id = "dsa",
            name = "DSA",
            description = "Crack algorithmic interviews with spatial pattern mastery",
            milestones = listOf(
                PathMilestone("dsa-1", "Beginner", "Arrays, Two-Pointers & Hash", "Sliding window, prefix sums, memory allocation and frequency maps.", 18, isCompleted = true, isUnlocked = true),
                PathMilestone("dsa-2", "Intermediate", "Linked Lists, Trees & BST", "Fast-slow pointers, recursive traversals, and level-order BFS/DFS.", 24, isCompleted = false, isUnlocked = true),
                PathMilestone("dsa-3", "Advanced", "Graphs & Dynamic Programming", "Dijkstra, topological sort, memoization tables & knapsack logic.", 30, isCompleted = false, isUnlocked = false),
                PathMilestone("dsa-4", "Expert", "Segment Trees, Tries & Bitmask", "Range query trees, persistent data structures & Bitmask DP.", 16, isCompleted = false, isUnlocked = false)
            )
        ),
        LearningPath(
            id = "ai-ml",
            name = "AI/ML",
            description = "Modern machine learning, neural networks, PyTorch & LLM tooling",
            milestones = listOf(
                PathMilestone("ai-1", "Beginner", "NumPy, Pandas & Linear Algebra", "Matrix multiplication, vectorization, tensor shapes and data cleaning.", 15, isCompleted = true, isUnlocked = true),
                PathMilestone("ai-2", "Intermediate", "Supervised & Unsupervised ML", "Gradient descent, logistic regression, random forests and clustering.", 20, isCompleted = false, isUnlocked = true),
                PathMilestone("ai-3", "Advanced", "PyTorch & Deep Neural Nets", "Backpropagation, CNNs for vision, RNNs and attention mechanisms.", 25, isCompleted = false, isUnlocked = false),
                PathMilestone("ai-4", "Expert", "LLM Agents & Fine-Tuning", "LoRA/QLoRA adapters, RAG architectures and function-calling agents.", 20, isCompleted = false, isUnlocked = false)
            )
        )
    )

    val recommendedCourses: List<RecommendedCourse> = listOf(
        RecommendedCourse("rc-1", "Clean Code & Refactoring Patterns", "Intermediate", "3.5 hrs", 45, "Engineering", 4.9f),
        RecommendedCourse("rc-2", "SQL & Relational DB Optimizations", "Beginner", "2.8 hrs", 10, "Databases", 4.8f),
        RecommendedCourse("rc-3", "Modern TypeScript Deep Dive", "Advanced", "5.1 hrs", 0, "Languages", 4.95f),
        RecommendedCourse("rc-4", "Prompt Engineering & LLM APIs", "Intermediate", "4.0 hrs", 75, "AI", 4.92f)
    )

    val badges: List<LearnerBadge> = listOf(
        LearnerBadge("b1", "Streak Master", "Gold", "Maintained a 5+ day streak", isUnlocked = true),
        LearnerBadge("b2", "Bug Smasher", "Silver", "Solved 10 test cases in first run", isUnlocked = true),
        LearnerBadge("b3", "Speed Demon", "Bronze", "Completed daily quest in under 3 mins", isUnlocked = true),
        LearnerBadge("b4", "Code Titan", "Diamond", "Reach 5,000 XP in Python or DSA", isUnlocked = false)
    )

    val notifications: List<HomeNotification> = listOf(
        HomeNotification("n1", "🔥 Streak Safe!", "You completed yesterday's quest on time.", "2h ago", isUnread = true),
        HomeNotification("n2", "⚡ Diamond League", "You advanced to Rank #6 today!", "5h ago", isUnread = true),
        HomeNotification("n3", "🎁 Daily Quest Ready", "Fresh non-repetitive challenges generated by Gemini AI.", "1d ago", isUnread = false)
    )
}
