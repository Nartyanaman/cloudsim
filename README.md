# ☁️ CloudSim 3.3 - Cost-Based Cloud Resource Optimization using Genetic Algorithm + Ollama LLM

This project simulates a cloud environment using **CloudSim 3.3** and optimizes **VM-to-cloudlet mapping** using a **Genetic Algorithm**, with **cost** as the fitness function. It also integrates with a **local LLM (Ollama)** to analyze simulation results intelligently.

---

## 📌 Key Features

- 🧪 Cloud simulation using **CloudSim 3.3**
- 🤖 Genetic Algorithm for optimal VM allocation
- 💰 Cost as the primary fitness metric
- 📈 Performance output (e.g., total cost, mapping)
- 📁 Load VM details (Cost, Reliability, Availability) from a CSV
- 🤝 Sends results to **Ollama LLM (e.g., Mistral)** for feedback

---

## 🧬 Genetic Algorithm - Overview

The GA follows these steps:

1. **Initialization**: Generate initial VM-cloudlet mappings randomly  
2. **Fitness Function**: Calculate total **cost** for each mapping  
3. **Selection**: Pick best mappings based on lowest cost  
4. **Crossover**: Combine pairs to produce new mappings  
5. **Mutation**: Randomly tweak mappings for diversity  
6. **Termination**: Stop after N generations or minimal improvement  

---

## ✅ Project Prerequisites

### 1. Install Requirements

- Java JDK 8 or higher  
- CloudSim 3.3 library  
- [Ollama](https://ollama.com/) installed with any model (e.g., `mistral`)  
- VM data file in CSV format  
- Java IDE or terminal  

---

## 🧾 Sample CSV: `vm_metrics.csv`

Place this file in your `input/` directory:

```csv
VM_ID,Cost,Reliability,Availability
0,0.05,0.95,0.98
1,0.07,0.97,0.99
2,0.09,0.93,0.97
```

---

## ⚙️ Ollama Setup

Make sure Ollama is installed and running with a model:

```bash
ollama run mistral
```

This will start a local server at:

```
http://localhost:11434
```

Test the connection:

```bash
curl http://localhost:11434
```

---

## 📡 Ollama API Endpoint

To send your simulation output:

```http
POST http://localhost:11434/api/generate
```

Example JSON body:

```json
{
  "model": "mistral",
  "prompt": "Analyze this simulation output: <your text here>"
}
```

---

## 📁 Suggested Project Structure

```
CloudSimExample/
├── src/
│   ├── cloudsim/
│   ├── org/
│   │   └── yourname/
│   │       └── cloudsimexample/
│   │           ├── CloudSimExample.java       <-- Main class
│   │           ├── CsvReader.java             <-- Reads CSV data
│   │           ├── OllamaConnector.java       <-- Sends output to Ollama
│   │           └── GeneticAlgorithm.java      <-- Handles optimization
├── input/
│   └── vm_metrics.csv
├── results/
│   └── output.txt
└── README.md
```

---

## ▶️ How to Run Your Project

### 1. Compile

Use this command in your terminal:

```bash
javac -cp .:cloudsim.jar src/org/yourname/cloudsimexample/*.java
```

> 🪟 On Windows, replace `:` with `;` in the classpath.

---

### 2. Run Simulation

```bash
java -cp .:cloudsim.jar src.org.yourname.cloudsimexample.CloudSimExample
```

---

### ✅ Output

- The simulation results will be printed in the console.
- A copy will be saved in:

```
results/output.txt
```

- The output will also be sent to **Ollama** via `OllamaConnector.java`.

---

## 📄 License

This project is licensed under the **MIT License**.

---


