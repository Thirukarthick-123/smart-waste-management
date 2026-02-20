import { useState } from "react";

function BinForm({ initialData, onSubmit, onCancel }) {
  const [form, setForm] = useState(
    initialData || {
      binCode: "",
      area: "",
      capacityLiters: "",
      status: "OK",
    }
  );

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  return (
    <div className="modal">
      <h3>{initialData ? "Update Bin" : "Add Bin"}</h3>

      <input
        name="binCode"
        placeholder="Bin Code"
        value={form.binCode}
        onChange={handleChange}
      />

      <input
        name="area"
        placeholder="Area"
        value={form.area}
        onChange={handleChange}
      />

      <input
        type="number"
        name="capacityLiters"
        placeholder="Capacity (Liters)"
        value={form.capacityLiters}
        onChange={handleChange}
      />

      <select name="status" value={form.status} onChange={handleChange}>
        <option value="OK">OK</option>
        <option value="NEAR_FULL">NEAR FULL</option>
        <option value="FULL">FULL</option>
        <option value="OFFLINE">OFFLINE</option>
      </select>

      <button onClick={() => onSubmit(form)}>Save</button>
      <button onClick={onCancel}>Cancel</button>
    </div>

    
    
  );
}

export default BinForm;
