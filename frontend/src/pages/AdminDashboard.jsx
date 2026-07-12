import React, { useState, useEffect } from 'react';
import API from '../api/client';
import VehicleCard from '../components/VehicleCard';
import VehicleFormModal from '../components/VehicleFormModal';
import RestockModal from '../components/RestockModal';
import { Plus, Shield, Receipt, RefreshCw, XCircle, Car, DollarSign, ShoppingBag, AlertTriangle, Flame, Download } from 'lucide-react';
import {
  ResponsiveContainer, PieChart, Pie, Cell,
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend,
  LineChart, Line
} from 'recharts';

const CHART_COLORS = ['#8b5cf6', '#10b981', '#f43f5e', '#f59e0b', '#3b82f6', '#ec4899', '#14b8a6'];

const AdminDashboard = () => {
  const [vehicles, setVehicles] = useState([]);
  const [auditLogs, setAuditLogs] = useState([]);
  const [analytics, setAnalytics] = useState(null);

  const [loading, setLoading] = useState(true);
  const [logsLoading, setLogsLoading] = useState(true);
  const [analyticsLoading, setAnalyticsLoading] = useState(true);

  // Modals overlays
  const [showFormModal, setShowFormModal] = useState(false);
  const [editingVehicle, setEditingVehicle] = useState(null);
  const [restockingVehicle, setRestockingVehicle] = useState(null);

  const [feedback, setFeedback] = useState({ type: '', message: '' });

  const fetchVehicles = async () => {
    setLoading(true);
    try {
      const response = await API.get('/api/vehicles');
      setVehicles(response.data);
    } catch (error) {
      console.error("Error fetching vehicles:", error);
    } finally {
      setLoading(false);
    }
  };

  const fetchAuditLogs = async () => {
    setLogsLoading(true);
    try {
      const response = await API.get('/api/purchases');
      setAuditLogs(response.data);
    } catch (error) {
      console.error("Error fetching audit logs:", error);
    } finally {
      setLogsLoading(false);
    }
  };

  const fetchAnalytics = async () => {
    setAnalyticsLoading(true);
    try {
      const response = await API.get('/api/analytics/summary');
      setAnalytics(response.data);
    } catch (error) {
      console.error("Error fetching analytics:", error);
    } finally {
      setAnalyticsLoading(false);
    }
  };

  const syncAllData = () => {
    fetchVehicles();
    fetchAuditLogs();
    fetchAnalytics();
  };

  useEffect(() => {
    syncAllData();
  }, []);

  const triggerFeedback = (type, message) => {
    setFeedback({ type, message });
    setTimeout(() => setFeedback({ type: '', message: '' }), 4000);
  };

  const handleDownloadInvoice = async (id) => {
    try {
      const response = await API.get(`/api/purchases/${id}/invoice`, {
        responseType: 'blob'
      });
      const url = window.URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `invoice-${id}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
    } catch (error) {
      console.error("Error downloading invoice:", error);
      alert("Failed to download invoice. Please try again.");
    }
  };

  const handleCreateSuccess = (newVehicle) => {
    setShowFormModal(false);
    triggerFeedback('success', `Successfully added new vehicle: ${newVehicle.year} ${newVehicle.make} ${newVehicle.model}`);
    syncAllData();
  };

  const handleEditSuccess = (updatedVehicle) => {
    setEditingVehicle(null);
    triggerFeedback('success', `Successfully updated vehicle details for ${updatedVehicle.year} ${updatedVehicle.make}`);
    syncAllData();
  };

  const handleRestockSuccess = (updatedVehicle) => {
    setRestockingVehicle(null);
    triggerFeedback('success', `Stock updated successfully. New quantity: ${updatedVehicle.stock}`);
    syncAllData();
  };

  const handleDelete = async (vehicleId) => {
    if (!window.confirm("Are you sure you want to remove this vehicle from inventory?")) {
      return;
    }

    try {
      await API.delete(`/api/vehicles/${vehicleId}`);
      triggerFeedback('success', 'Vehicle removed from inventory successfully');
      syncAllData();
    } catch (error) {
      console.error("Deletion failed:", error);
      const message = error.response?.data?.message || 'Failed to delete vehicle';
      triggerFeedback('error', message);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-6 py-8 space-y-10">
      {/* Header Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between bg-gradient-to-r from-slate-900 to-indigo-950 border border-slate-800 rounded-3xl p-8 shadow-xl space-y-4 md:space-y-0">
        <div className="flex items-center space-x-4">
          <div className="bg-rose-500/10 p-3 rounded-2xl border border-rose-500/20 text-rose-400">
            <Shield className="h-8 w-8" />
          </div>
          <div>
            <h1 className="text-2xl font-extrabold text-white flex items-center space-x-2">
              <span>Admin Panel</span>
              <span className="text-xs bg-rose-500/25 text-rose-450 border border-rose-500/35 px-2.5 py-0.5 rounded-full font-bold uppercase tracking-wider">Manager</span>
            </h1>
            <p className="text-slate-400 text-xs mt-1">Manage dealership stock, edit specs, and audit transactions.</p>
          </div>
        </div>

        <button
          onClick={() => setShowFormModal(true)}
          className="flex items-center justify-center space-x-2 bg-gradient-to-r from-violet-650 to-indigo-600 hover:from-violet-500 hover:to-indigo-500 text-white font-semibold py-3 px-5 rounded-xl shadow-lg shadow-violet-600/10 transition-all duration-300 transform active:scale-98 cursor-pointer text-sm"
        >
          <Plus className="h-4.5 w-4.5" />
          <span>Add New Vehicle</span>
        </button>
      </div>

      {/* Alerts */}
      {feedback.message && (
        <div className={`px-6 py-4 rounded-2xl flex items-center justify-between shadow-lg text-sm font-semibold animate-bounce ${
          feedback.type === 'success' ? 'bg-emerald-500/10 border border-emerald-500/25 text-emerald-400 shadow-emerald-950/10' : 'bg-rose-500/10 border border-rose-500/25 text-rose-450 shadow-rose-950/10'
        }`}>
          <span>{feedback.message}</span>
          <button onClick={() => setFeedback({ type: '', message: '' })} className="text-slate-400 hover:text-slate-200 cursor-pointer">
            <XCircle className="h-5 w-5" />
          </button>
        </div>
      )}

      {/* Analytics Dashboard Grid */}
      <div className="space-y-6">
        <h2 className="text-xl font-bold text-white border-l-4 border-violet-500 pl-3">Analytics Dashboard</h2>

        {analyticsLoading ? (
          <div className="grid grid-cols-1 md:grid-cols-5 gap-6">
            {[1, 2, 3, 4, 5].map((idx) => (
              <div key={idx} className="bg-slate-900/20 border border-slate-850 rounded-2xl h-28 animate-pulse"></div>
            ))}
          </div>
        ) : analytics ? (
          <div className="space-y-8">
            {/* KPI Cards */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-6">
              {/* Card 1: Total Stock Units */}
              <div className="bg-slate-900/40 border border-slate-800 rounded-2xl p-5 flex items-center space-x-4">
                <div className="p-3 rounded-xl bg-violet-500/10 text-violet-400 border border-violet-500/20">
                  <Car className="h-6 w-6" />
                </div>
                <div>
                  <div className="text-xs text-slate-400 font-semibold uppercase tracking-wider">Total Vehicles</div>
                  <div className="text-2xl font-bold text-white mt-0.5">{analytics.totalVehicles} <span className="text-xs text-slate-500 font-normal">units</span></div>
                </div>
              </div>

              {/* Card 2: Total Inventory Value */}
              <div className="bg-slate-900/40 border border-slate-800 rounded-2xl p-5 flex items-center space-x-4">
                <div className="p-3 rounded-xl bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                  <DollarSign className="h-6 w-6" />
                </div>
                <div>
                  <div className="text-xs text-slate-400 font-semibold uppercase tracking-wider">Inventory Value</div>
                  <div className="text-xl font-bold text-white mt-0.5">${analytics.totalInventoryValue.toLocaleString()}</div>
                </div>
              </div>

              {/* Card 3: Total Sold Vehicles */}
              <div className="bg-slate-900/40 border border-slate-800 rounded-2xl p-5 flex items-center space-x-4">
                <div className="p-3 rounded-xl bg-blue-500/10 text-blue-400 border border-blue-500/20">
                  <ShoppingBag className="h-6 w-6" />
                </div>
                <div>
                  <div className="text-xs text-slate-400 font-semibold uppercase tracking-wider">Sold Vehicles</div>
                  <div className="text-2xl font-bold text-white mt-0.5">{analytics.totalSoldVehicles} <span className="text-xs text-slate-500 font-normal">units</span></div>
                </div>
              </div>

              {/* Card 4: Low Stock Alert */}
              <div className={`border rounded-2xl p-5 flex items-center space-x-4 transition-colors duration-300 ${
                analytics.lowStockVehicles > 0
                  ? 'bg-amber-500/10 border-amber-500/30'
                  : 'bg-slate-900/40 border-slate-800'
              }`}>
                <div className={`p-3 rounded-xl border ${
                  analytics.lowStockVehicles > 0
                    ? 'bg-amber-500/20 text-amber-400 border-amber-500/30 animate-pulse'
                    : 'bg-slate-800 text-slate-400 border-slate-700'
                }`}>
                  <AlertTriangle className="h-6 w-6" />
                </div>
                <div>
                  <div className="text-xs text-slate-400 font-semibold uppercase tracking-wider">Low Stock Models</div>
                  <div className={`text-2xl font-bold mt-0.5 ${analytics.lowStockVehicles > 0 ? 'text-amber-400' : 'text-white'}`}>
                    {analytics.lowStockVehicles}
                  </div>
                </div>
              </div>

              {/* Card 5: Out of Stock Alert */}
              <div className={`border rounded-2xl p-5 flex items-center space-x-4 transition-colors duration-300 ${
                analytics.outOfStockVehicles > 0
                  ? 'bg-rose-500/10 border-rose-500/30'
                  : 'bg-slate-900/40 border-slate-800'
              }`}>
                <div className={`p-3 rounded-xl border ${
                  analytics.outOfStockVehicles > 0
                    ? 'bg-rose-500/20 text-rose-450 border-rose-500/30 animate-pulse'
                    : 'bg-slate-800 text-slate-400 border-slate-700'
                }`}>
                  <Flame className="h-6 w-6" />
                </div>
                <div>
                  <div className="text-xs text-slate-400 font-semibold uppercase tracking-wider">Out of Stock</div>
                  <div className={`text-2xl font-bold mt-0.5 ${analytics.outOfStockVehicles > 0 ? 'text-rose-400' : 'text-white'}`}>
                    {analytics.outOfStockVehicles}
                  </div>
                </div>
              </div>
            </div>

            {/* Recharts Graphical Visuals */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
              {/* Pie Chart: Category Distribution */}
              <div className="bg-slate-900/40 border border-slate-800 rounded-3xl p-6 shadow-md flex flex-col justify-between h-[360px]">
                <h3 className="text-sm font-bold text-slate-300 uppercase tracking-wider mb-4 border-b border-slate-800 pb-2">Category Distribution</h3>
                <div className="flex-1 min-h-0">
                  {analytics.categoryDistribution.length === 0 ? (
                    <div className="h-full flex items-center justify-center text-slate-500 text-xs">No Data Available</div>
                  ) : (
                    <ResponsiveContainer width="100%" height="100%">
                      <PieChart>
                        <Pie
                          data={analytics.categoryDistribution}
                          cx="50%"
                          cy="50%"
                          innerRadius={60}
                          outerRadius={90}
                          paddingAngle={5}
                          dataKey="value"
                        >
                          {analytics.categoryDistribution.map((entry, index) => (
                            <Cell key={`cell-${index}`} fill={CHART_COLORS[index % CHART_COLORS.length]} />
                          ))}
                        </Pie>
                        <Tooltip
                          contentStyle={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderRadius: '12px' }}
                          itemStyle={{ color: '#f1f5f9' }}
                        />
                        <Legend iconType="circle" wrapperStyle={{ fontSize: '11px', paddingTop: '10px' }} />
                      </PieChart>
                    </ResponsiveContainer>
                  )}
                </div>
              </div>

              {/* Bar Chart: Inventory Value by Category */}
              <div className="bg-slate-900/40 border border-slate-800 rounded-3xl p-6 shadow-md flex flex-col justify-between h-[360px]">
                <h3 className="text-sm font-bold text-slate-300 uppercase tracking-wider mb-4 border-b border-slate-800 pb-2">Inventory Value by Category</h3>
                <div className="flex-1 min-h-0">
                  {analytics.inventoryByCategory.length === 0 ? (
                    <div className="h-full flex items-center justify-center text-slate-500 text-xs">No Data Available</div>
                  ) : (
                    <ResponsiveContainer width="100%" height="100%">
                      <BarChart data={analytics.inventoryByCategory}>
                        <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
                        <XAxis dataKey="name" stroke="#64748b" fontSize={11} tickLine={false} />
                        <YAxis stroke="#64748b" fontSize={11} tickLine={false} />
                        <Tooltip
                          formatter={(value) => [`$${value.toLocaleString()}`, 'Value']}
                          contentStyle={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderRadius: '12px' }}
                          itemStyle={{ color: '#f1f5f9' }}
                        />
                        <Bar dataKey="value" fill="#10b981" radius={[8, 8, 0, 0]} maxBarSize={40} />
                      </BarChart>
                    </ResponsiveContainer>
                  )}
                </div>
              </div>

              {/* Line Chart: Monthly Sales Trends */}
              <div className="bg-slate-900/40 border border-slate-800 rounded-3xl p-6 shadow-md flex flex-col justify-between h-[360px]">
                <h3 className="text-sm font-bold text-slate-300 uppercase tracking-wider mb-4 border-b border-slate-800 pb-2">Monthly Units Sold</h3>
                <div className="flex-1 min-h-0">
                  <ResponsiveContainer width="100%" height="100%">
                    <LineChart data={analytics.monthlySales}>
                      <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
                      <XAxis dataKey="name" stroke="#64748b" fontSize={11} tickLine={false} />
                      <YAxis stroke="#64748b" fontSize={11} tickLine={false} />
                      <Tooltip
                        contentStyle={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderRadius: '12px' }}
                        itemStyle={{ color: '#f1f5f9' }}
                      />
                      <Line type="monotone" dataKey="value" stroke="#8b5cf6" strokeWidth={3} activeDot={{ r: 6 }} name="Units Sold" />
                    </LineChart>
                  </ResponsiveContainer>
                </div>
              </div>
            </div>
          </div>
        ) : (
          <div className="bg-slate-900/20 border border-slate-850 rounded-2xl h-28 flex items-center justify-center text-slate-500">Failed to load analytics dashboard.</div>
        )}
      </div>

      {/* Inventory Management Section */}
      <div className="space-y-6">
        <div className="flex items-center justify-between border-l-4 border-rose-500 pl-3">
          <h2 className="text-xl font-bold text-white">Live Inventory</h2>
          <button onClick={syncAllData} className="text-slate-400 hover:text-slate-200 flex items-center space-x-1 text-xs cursor-pointer bg-transparent border-0">
            <RefreshCw className="h-3 w-3" />
            <span>Sync</span>
          </button>
        </div>

        {loading ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {[1, 2, 3].map((idx) => (
              <div key={idx} className="bg-slate-900/20 border border-slate-850 rounded-2xl h-[360px] animate-pulse"></div>
            ))}
          </div>
        ) : vehicles.length === 0 ? (
          <div className="bg-slate-900/30 border border-slate-850 rounded-2xl p-12 text-center text-slate-400">
            No vehicles in stock. Click "Add New Vehicle" to seed inventory catalog!
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {vehicles.map((v) => (
              <VehicleCard
                key={v.id}
                vehicle={v}
                isAdmin={true}
                onEdit={setEditingVehicle}
                onRestock={setRestockingVehicle}
                onDelete={handleDelete}
              />
            ))}
          </div>
        )}
      </div>

      {/* Global Sales Audit Logs */}
      <div className="space-y-6">
        <div className="flex items-center space-x-2 border-l-4 border-rose-500 pl-3">
          <Receipt className="h-5 w-5 text-rose-400" />
          <h2 className="text-xl font-bold text-white">System Purchase History (Audit Logs)</h2>
        </div>

        {logsLoading ? (
          <div className="bg-slate-900/20 border border-slate-850 rounded-2xl h-32 animate-pulse"></div>
        ) : auditLogs.length === 0 ? (
          <div className="bg-slate-900/30 border border-slate-850 rounded-2xl p-8 text-center text-slate-400 text-sm">
            No transaction records found.
          </div>
        ) : (
          <div className="bg-slate-900/40 border border-slate-800 rounded-2xl overflow-hidden shadow-lg">
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse text-sm">
                <thead>
                  <tr className="bg-slate-950/40 border-b border-slate-800 text-slate-400 font-semibold">
                    <th className="p-4">Transaction ID</th>
                    <th className="p-4">Client User ID</th>
                    <th className="p-4">Vehicle ID</th>
                    <th className="p-4">Price per Unit</th>
                    <th className="p-4">Quantity</th>
                    <th className="p-4">Total Amount</th>
                    <th className="p-4">Transaction Date</th>
                    <th className="p-4 text-right">Invoice</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/60 text-slate-300">
                  {auditLogs.map((record) => (
                    <tr key={record.id} className="hover:bg-slate-850/30 transition-colors duration-200">
                      <td className="p-4 font-mono text-xs text-rose-450">{record.id}</td>
                      <td className="p-4 font-mono text-xs">{record.userId}</td>
                      <td className="p-4 font-mono text-xs">{record.vehicleId}</td>
                      <td className="p-4">${record.purchasePrice.toLocaleString()}</td>
                      <td className="p-4">{record.quantity}</td>
                      <td className="p-4 font-bold text-white">${record.totalAmount.toLocaleString()}</td>
                      <td className="p-4 text-xs text-slate-400">
                        {new Date(record.purchasedAt).toLocaleString()}
                      </td>
                      <td className="p-4 text-right">
                        <button
                          onClick={() => handleDownloadInvoice(record.id)}
                          className="inline-flex items-center space-x-1 bg-rose-500/10 hover:bg-rose-500/20 text-rose-400 font-semibold px-2.5 py-1.5 rounded-lg border border-rose-500/20 hover:border-rose-500/35 transition-all text-xs cursor-pointer"
                        >
                          <Download className="h-3.5 w-3.5" />
                          <span>PDF</span>
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>

      {/* Vehicle Form Modal Overlay */}
      {(showFormModal || editingVehicle) && (
        <VehicleFormModal
          vehicle={editingVehicle}
          onClose={() => {
            setShowFormModal(false);
            setEditingVehicle(null);
          }}
          onSuccess={editingVehicle ? handleEditSuccess : handleCreateSuccess}
        />
      )}

      {/* Restock Modal Overlay */}
      {restockingVehicle && (
        <RestockModal
          vehicle={restockingVehicle}
          onClose={() => setRestockingVehicle(null)}
          onSuccess={handleRestockSuccess}
        />
      )}
    </div>
  );
};

export default AdminDashboard;
