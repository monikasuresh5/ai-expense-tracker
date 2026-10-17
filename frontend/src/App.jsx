import { useEffect, useState } from "react";
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from "recharts";
import api from "./api";
import "./App.css";

const CATEGORIES = ["FOOD", "TRAVEL", "BILLS", "SHOPPING", "ENTERTAINMENT", "HEALTH", "EDUCATION", "OTHER"];
const COLORS = ["#4f46e5", "#06b6d4", "#f59e0b", "#ef4444", "#10b981", "#8b5cf6", "#ec4899", "#6b7280"];

const today = () => new Date().toLocaleDateString("en-CA"); // yyyy-mm-dd in local time
const emptyForm = () => ({ title: "", amount: "", category: "", date: today(), description: "" });

function AuthForm({ onLoggedIn }) {
  const [mode, setMode] = useState("login");
  const [form, setForm] = useState({ name: "", email: "", password: "" });
  const [error, setError] = useState("");

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      if (mode === "register") await api.post("/users/register", form);
      const res = await api.post("/users/login", { email: form.email, password: form.password });
      localStorage.setItem("token", res.data.token);
      localStorage.setItem("email", res.data.email);
      onLoggedIn();
    } catch (err) {
      setError(err.response?.data?.error || "Could not reach the server");
    }
  };

  return (
    <div className="auth-wrap">
      <form className="card auth-card" onSubmit={submit}>
        <h1>AI Expense Tracker</h1>
        <p className="muted">{mode === "login" ? "Log in to continue" : "Create your account"}</p>
        {mode === "register" && (
          <input name="name" placeholder="Name" value={form.name} onChange={handleChange} required />
        )}
        <input name="email" type="email" placeholder="Email" value={form.email} onChange={handleChange} required />
        <input name="password" type="password" placeholder="Password" value={form.password} onChange={handleChange} required />
        {error && <p className="error">{error}</p>}
        <button type="submit">{mode === "login" ? "Log in" : "Sign up"}</button>
        <p
          className="muted switch"
          onClick={() => {
            setMode(mode === "login" ? "register" : "login");
            setError("");
          }}
        >
          {mode === "login" ? "New here? Create an account" : "Already have an account? Log in"}
        </p>
      </form>
    </div>
  );
}

function Dashboard({ onLogout }) {
  const [expenses, setExpenses] = useState([]);
  const [form, setForm] = useState(emptyForm());
  const [editingId, setEditingId] = useState(null);
  const [summary, setSummary] = useState("");
  const [busy, setBusy] = useState("");
  const [error, setError] = useState("");

  const load = async () => {
    const res = await api.get("/expenses");
    setExpenses(res.data);
  };

  useEffect(() => {
    load().catch(() => setError("Could not load expenses"));
  }, []);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const save = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const payload = { ...form, amount: parseFloat(form.amount) };
      if (editingId) await api.put(`/expenses/${editingId}`, payload);
      else await api.post("/expenses", payload);
      setForm(emptyForm());
      setEditingId(null);
      await load();
    } catch (err) {
      setError(err.response?.data?.error || "Could not save expense");
    }
  };

  const startEdit = (x) => {
    setEditingId(x.id);
    setForm({
      title: x.title,
      amount: String(x.amount),
      category: x.category,
      date: x.date,
      description: x.description || "",
    });
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const cancelEdit = () => {
    setEditingId(null);
    setForm(emptyForm());
  };

  const remove = async (id) => {
    if (!window.confirm("Delete this expense?")) return;
    await api.delete(`/expenses/${id}`);
    await load();
  };

  const suggest = async () => {
    if (!form.title) return;
    setBusy("suggest");
    setError("");
    try {
      const res = await api.post("/ai/categorize", { title: form.title, description: form.description });
      setForm((f) => ({ ...f, category: res.data.category }));
    } catch (err) {
      setError(err.response?.data?.error || "AI suggestion failed");
    } finally {
      setBusy("");
    }
  };

  const getSummary = async () => {
    setBusy("summary");
    setError("");
    try {
      const res = await api.get("/ai/summary");
      setSummary(res.data.summary);
    } catch (err) {
      setError(err.response?.data?.error || "Could not get summary");
    } finally {
      setBusy("");
    }
  };

  const total = expenses.reduce((s, x) => s + x.amount, 0);
  const byCategory = Object.values(
    expenses.reduce((acc, x) => {
      const key = x.category || "OTHER";
      acc[key] = acc[key] || { name: key, value: 0 };
      acc[key].value += x.amount;
      return acc;
    }, {})
  );

  return (
    <div className="container">
      <header>
        <h1>💸 AI Expense Tracker</h1>
        <div>
          <span className="muted">{localStorage.getItem("email")}</span>
          <button className="secondary" onClick={onLogout}>Log out</button>
        </div>
      </header>

      <div className="stats">
        <div className="card">
          <p className="muted">Total spent</p>
          <h2>₹{total.toFixed(2)}</h2>
        </div>
        <div className="card">
          <p className="muted">Expenses</p>
          <h2>{expenses.length}</h2>
        </div>
      </div>

      <form className="card form" onSubmit={save}>
        <h3>{editingId ? "Edit expense" : "Add expense"}</h3>
        <div className="row">
          <input name="title" placeholder="Title (e.g. Uber to college)" value={form.title} onChange={handleChange} required />
          <input name="amount" type="number" step="0.01" min="0.01" placeholder="Amount" value={form.amount} onChange={handleChange} required />
        </div>
        <div className="row">
          <select name="category" value={form.category} onChange={handleChange} required>
            <option value="">Select category</option>
            {CATEGORIES.map((c) => (
              <option key={c} value={c}>{c}</option>
            ))}
          </select>
          <button type="button" className="secondary" onClick={suggest} disabled={busy === "suggest" || !form.title}>
            {busy === "suggest" ? "Thinking..." : "✨ Suggest category"}
          </button>
          <input name="date" type="date" value={form.date} onChange={handleChange} required />
        </div>
        <input name="description" placeholder="Note (optional)" value={form.description} onChange={handleChange} />
        {error && <p className="error">{error}</p>}
        <div className="row">
          <button type="submit">{editingId ? "Update" : "Add expense"}</button>
          {editingId && (
            <button type="button" className="secondary" onClick={cancelEdit}>Cancel</button>
          )}
        </div>
      </form>

      <div className="grid">
        <div className="card">
          <h3>Spending by category</h3>
          {byCategory.length === 0 ? (
            <p className="muted">Add an expense to see the chart.</p>
          ) : (
            <ResponsiveContainer width="100%" height={260}>
              <PieChart>
                <Pie data={byCategory} dataKey="value" nameKey="name" outerRadius={90}>
                  {byCategory.map((_, i) => (
                    <Cell key={i} fill={COLORS[i % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip formatter={(v) => `₹${Number(v).toFixed(2)}`} />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          )}
        </div>

        <div className="card">
          <h3>AI monthly insights</h3>
          <button onClick={getSummary} disabled={busy === "summary"}>
            {busy === "summary" ? "Analyzing..." : "✨ Summarize this month"}
          </button>
          {summary && <p className="summary">{summary}</p>}
        </div>
      </div>

      <div className="card">
        <h3>Your expenses</h3>
        {expenses.length === 0 ? (
          <p className="muted">No expenses yet.</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Date</th>
                <th>Title</th>
                <th>Category</th>
                <th>Amount</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {expenses.map((x) => (
                <tr key={x.id}>
                  <td>{x.date}</td>
                  <td>{x.title}</td>
                  <td><span className="tag">{x.category}</span></td>
                  <td>₹{x.amount.toFixed(2)}</td>
                  <td className="actions">
                    <button className="link" onClick={() => startEdit(x)}>Edit</button>
                    <button className="link danger" onClick={() => remove(x.id)}>Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}

export default function App() {
  const [loggedIn, setLoggedIn] = useState(!!localStorage.getItem("token"));

  const logout = () => {
    localStorage.clear();
    setLoggedIn(false);
  };

  return loggedIn ? <Dashboard onLogout={logout} /> : <AuthForm onLoggedIn={() => setLoggedIn(true)} />;
}