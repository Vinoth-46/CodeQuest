// CodeQuest Mock Data Layer
export const userData = {
  name: "Vinoth",
  streak: 5,
  streakActive: true,
  avatar: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80",
  notificationsCount: 3,
  notifications: [
    { id: 1, title: "🔥 Streak Safe!", desc: "You completed yesterday's quest on time.", time: "2h ago", unread: true },
    { id: 2, title: "⚡ Diamond League", desc: "You advanced to Rank #6 today!", time: "5h ago", unread: true },
    { id: 3, title: "🎁 Daily Gift Ready", desc: "Claim 15 free gems in the arena store.", time: "1d ago", unread: false }
  ],
  stats: {
    xp: 2840,
    nextLevelXp: 3500,
    level: 12,
    rankTitle: "Logic Architect",
    lessonsCompleted: 48,
    accuracy: "96.4%",
    gems: 320,
    globalRank: "#6"
  },
  badges: [
    { id: "b1", name: "Streak Master", tier: "Gold", icon: "Flame", unlocked: true, desc: "Maintained a 5+ day streak" },
    { id: "b2", name: "Bug Smasher", tier: "Silver", icon: "CheckCircle2", unlocked: true, desc: "Solved 10 test cases in first run" },
    { id: "b3", name: "Speed Demon", tier: "Bronze", icon: "Zap", unlocked: true, desc: "Completed daily quest in under 3 mins" },
    { id: "b4", name: "Code Titan", tier: "Diamond", icon: "Trophy", unlocked: false, desc: "Reach 5,000 XP in Python or DSA" }
  ]
};

export const continueLearningData = {
  hasStarted: true, // Toggleable to test empty / "Start here" state
  course: {
    id: "py-fullstack",
    title: "Python Async & High-Speed APIs",
    path: "Python",
    currentLesson: "Lesson 7: Asyncio Event Loops & Coroutines",
    progress: 68,
    completedLessons: 17,
    totalLessons: 25,
    estimatedMinutesLeft: 14,
    codeSnippetPreview: "async def fetch_payload(session, url): ..."
  },
  starterCard: {
    title: "Welcome to CodeQuest!",
    subtitle: "You haven't enrolled in any tracks yet.",
    recommendation: "Begin with Python Essentials or Modern Web Dev to start your quest.",
    actionText: "Start Learning"
  }
};

export const dailyChallengeData = {
  id: "dc-2026-10-20",
  title: "Two-Pointer Palindrome Checker",
  subtitle: "Optimize O(1) space complexity by skipping non-alphanumeric chars.",
  category: "DSA & Algorithms",
  difficulty: "Intermediate",
  xpReward: 60,
  gemReward: 15,
  progressPercentage: 65,
  currentStepText: "3 of 4 test suites passing",
  timeLeftHours: 6,
  tags: ["Pointers", "String Tracing", "Clean Code"]
};

export const learningPathsData = [
  {
    id: "web-dev",
    name: "Web Dev",
    icon: "Globe",
    accentColor: "#6366F1",
    description: "Modern Fullstack JavaScript, React, Next.js & Serverless Systems",
    roadmap: [
      {
        id: "wd-1",
        tier: "Beginner",
        title: "HTML5, Modern CSS & JS",
        description: "Semantic layouts, Flexbox/Grid, and ES6+ DOM manipulation.",
        lessonsCount: 16,
        status: "completed"
      },
      {
        id: "wd-2",
        tier: "Intermediate",
        title: "React & Component Trees",
        description: "Hooks, state lifecycles, props drilling & custom event flows.",
        lessonsCount: 22,
        status: "unlocked"
      },
      {
        id: "wd-3",
        tier: "Advanced",
        title: "Fullstack Next.js & SSR",
        description: "Server components, streaming mutations & route handlers.",
        lessonsCount: 28,
        status: "locked"
      },
      {
        id: "wd-4",
        tier: "Expert",
        title: "Micro-frontends & Scale",
        description: "Edge caching, CDN strategies & zero-bundle microservices.",
        lessonsCount: 18,
        status: "locked"
      }
    ]
  },
  {
    id: "python",
    name: "Python",
    icon: "FileCode2",
    accentColor: "#3B82F6",
    description: "From syntax fundamentals to async frameworks & backend architecture",
    roadmap: [
      {
        id: "py-1",
        tier: "Beginner",
        title: "Syntax, Loops & Types",
        description: "Variables, lists, dicts, conditionals and core data structures.",
        lessonsCount: 14,
        status: "completed"
      },
      {
        id: "py-2",
        tier: "Intermediate",
        title: "OOP, Dunder Methods & Files",
        description: "Classes, inheritance, context managers and robust exception handling.",
        lessonsCount: 20,
        status: "unlocked"
      },
      {
        id: "py-3",
        tier: "Advanced",
        title: "Asyncio & FastAPI Services",
        description: "Event loops, coroutines, Pydantic schemas and dependency injection.",
        lessonsCount: 26,
        status: "locked"
      },
      {
        id: "py-4",
        tier: "Expert",
        title: "Distributed Python & GIL",
        description: "Multiprocessing, Cython bindings and high-throughput pipelines.",
        lessonsCount: 15,
        status: "locked"
      }
    ]
  },
  {
    id: "dsa",
    name: "DSA",
    icon: "Network",
    accentColor: "#10B981",
    description: "Crack FAANG-style algorithmic interviews with spatial pattern mastery",
    roadmap: [
      {
        id: "dsa-1",
        tier: "Beginner",
        title: "Arrays, Two-Pointers & Hash",
        description: "Sliding window, prefix sums, memory allocation and frequency maps.",
        lessonsCount: 18,
        status: "completed"
      },
      {
        id: "dsa-2",
        tier: "Intermediate",
        title: "Linked Lists, Trees & BST",
        description: "Fast-slow pointers, recursive traversals, and level-order BFS/DFS.",
        lessonsCount: 24,
        status: "unlocked"
      },
      {
        id: "dsa-3",
        tier: "Advanced",
        title: "Graphs & Dynamic Programming",
        description: "Dijkstra, topological sort, memoization tables & knapsack logic.",
        lessonsCount: 30,
        status: "locked"
      },
      {
        id: "dsa-4",
        tier: "Expert",
        title: "Segment Trees, Tries & Aho",
        description: "Range query trees, persistent data structures & Bitmask DP.",
        lessonsCount: 16,
        status: "locked"
      }
    ]
  },
  {
    id: "ai-ml",
    name: "AI/ML",
    icon: "BrainCircuit",
    accentColor: "#EC4899",
    description: "Modern machine learning, neural networks, PyTorch & LLM tooling",
    roadmap: [
      {
        id: "ai-1",
        tier: "Beginner",
        title: "NumPy, Pandas & Linear Algebra",
        description: "Matrix multiplication, vectorization, tensor shapes and data cleaning.",
        lessonsCount: 15,
        status: "completed"
      },
      {
        id: "ai-2",
        tier: "Intermediate",
        title: "Supervised & Unsupervised ML",
        description: "Gradient descent, logistic regression, random forests and clustering.",
        lessonsCount: 20,
        status: "unlocked"
      },
      {
        id: "ai-3",
        tier: "Advanced",
        title: "PyTorch & Deep Neural Nets",
        description: "Backpropagation, CNNs for vision, RNNs and attention mechanisms.",
        lessonsCount: 25,
        status: "locked"
      },
      {
        id: "ai-4",
        tier: "Expert",
        title: "LLM Agents & Fine-Tuning",
        description: "LoRA/QLoRA adapters, RAG architectures and function-calling agents.",
        lessonsCount: 20,
        status: "locked"
      }
    ]
  }
];

export const recommendedCourses = [
  {
    id: "rc-1",
    title: "Clean Code & Refactoring Patterns",
    level: "Intermediate",
    levelColor: "#3B82F6",
    duration: "3.5 hrs",
    progress: 45,
    tag: "Software Engineering",
    enrolledCount: "14.2k",
    rating: 4.9,
    icon: "Sparkles"
  },
  {
    id: "rc-2",
    title: "SQL & Relational DB Optimizations",
    level: "Beginner",
    levelColor: "#10B981",
    duration: "2.8 hrs",
    progress: 10,
    tag: "Databases",
    enrolledCount: "9.8k",
    rating: 4.8,
    icon: "Database"
  },
  {
    id: "rc-3",
    title: "Modern TypeScript Deep Dive",
    level: "Advanced",
    levelColor: "#8B5CF6",
    duration: "5.1 hrs",
    progress: 0,
    tag: "Language Deep Dive",
    enrolledCount: "21.5k",
    rating: 4.95,
    icon: "FileCode"
  },
  {
    id: "rc-4",
    title: "Prompt Engineering & LLM APIs",
    level: "Intermediate",
    levelColor: "#EC4899",
    duration: "4.0 hrs",
    progress: 75,
    tag: "Generative AI",
    enrolledCount: "33.1k",
    rating: 4.92,
    icon: "Bot"
  }
];

export const navigationTabs = [
  { id: "home", label: "Home", icon: "Home" },
  { id: "learn", label: "Learn", icon: "BookOpen" },
  { id: "practice", label: "Practice", icon: "Code2" },
  { id: "leaderboard", label: "Leaderboard", icon: "Trophy" },
  { id: "profile", label: "Profile", icon: "User" }
];
